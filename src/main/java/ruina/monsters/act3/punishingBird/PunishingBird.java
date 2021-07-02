package ruina.monsters.act3.punishingBird;

import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.PunishingBirdPunishmentPower;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class PunishingBird extends AbstractRuinaMonster {
    public static final String ID = makeID(PunishingBird.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    public static final String CAGE = RuinaMod.makeMonsterPath("PunishingBird/Cage.png");
    private static final Texture CAGE_TEXTURE = TexLoader.getTexture(CAGE);

    private static final byte PECK = 0;
    private static final byte PUNISHMENT = 1;

    private final int STATUS = calcAscensionSpecial(1);

    private boolean playingDeathAnimation = false;

    public PunishingBird() {
        this(100.0f, 0.0f);
    }

    public PunishingBird(final float x, final float y) {
        super(NAME, ID, 150, -5.0F, 0, 160.0f, 305.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("PunishingBird/Spriter/SmallBird.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(PECK, Intent.ATTACK_DEBUFF, calcAscensionDamage(2), 3, true);
        addMove(PUNISHMENT, Intent.ATTACK, calcAscensionSpecial(calcAscensionDamage(50)));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;
        if (info.base > -1) {
            info.applyPowers(this, adp());
        }
        switch (nextMove) {
            case PECK:
                for (int i = 0; i < multiplier; i++) {
                    peckAnimation(adp());
                    dmg(adp(), info);
                    recoilAnimation();
                    waitAnimation(0.25f);
                }
                resetIdle(0.0f);
                intoDrawMo(new Wound(), STATUS, this);
                break;
            case PUNISHMENT:
                punishAnimation(adp());
                dmg(adp(), info);
                resetIdle(1.0f);
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        PunishingBirdPunishmentPower punishment = (PunishingBirdPunishmentPower) this.getPower(PunishingBirdPunishmentPower.POWER_ID);
        if (punishment != null) {
            if (punishment.getPunishment()) {
                setMoveShortcut(PUNISHMENT, MOVES[1]);
                punishment.setPunishment(false);
                punishment.updateDescription();
            } else {
                setMoveShortcut(PECK, MOVES[0]);
            }
        } else {
            setMoveShortcut(PECK, MOVES[0]);
        }
    }

    @Override
    public void usePreBattleAction() {
        atb(new ApplyPowerAction(this, this, new PunishingBirdPunishmentPower(this)));
        Summon();
    }

    public void Summon() {
        float xPos_Farthest_L = -450F;
        float xPos_Middle_L = -150F;
        for (int i = 0; i < 2; i++) {
            AbstractMonster keeper;
            if (i == 0) {
                keeper = new Keeper(xPos_Farthest_L, 0.0f);
            } else  {
                keeper = new Keeper(xPos_Middle_L, 0.0f);
            }
            atb(new SpawnMonsterAction(keeper, true));
            atb(new UsePreBattleActionAction(keeper));
            if (!firstMove) {
                keeper.rollMove();
                keeper.createIntent();
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        if (!playingDeathAnimation) {
            playingDeathAnimation = true;
            for (AbstractMonster mo : monsterList()) {
                if (mo instanceof Keeper) {
                    atb(new SuicideAction(mo));
                }
            }
            cageEffect();
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    PunishingBird.super.die(triggerRelics);
                    this.isDone = true;
                }
            });
        }
    }

    private void peckAnimation(AbstractCreature enemy) {
        animationAction("Peck", "SmallBirdPeck", enemy, this);
    }

    private void recoilAnimation() {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), 0.25f));
        animationAction("Recoil", null, this);
    }

    private void punishAnimation(AbstractCreature enemy) {
        animationAction("Punish", "SmallBirdPunish", enemy, this);
    }

    private void cageEffect() {
        float duration = 1.0f;
        AbstractGameEffect houseEffect = new VfxBuilder(CAGE_TEXTURE, this.hb.cX, 0f, duration)
                .moveY(Settings.HEIGHT, this.hb.y + this.hb.height / 6, VfxBuilder.Interpolations.EXP5IN)
                .playSoundAt(duration, makeID("SmallBirdCage"))
                .build();
        atb(new VFXAction(houseEffect, duration));
    }

}