package ruina.monsters.act3.punishingBird;

import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act3.PunishingBirdPunishmentPower;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class PunishingBird extends AbstractRuinaMonster {
    public static final String ID = makeID(PunishingBird.class.getSimpleName());

    public static final String CAGE = RuinaMod.makeMonsterPath("PunishingBird/Cage.png");
    private static final Texture CAGE_TEXTURE = TexLoader.getTexture(CAGE);

    private static final byte PECK = 0;
    private static final byte PUNISHMENT = 1;

    private final int STATUS = 1;
    private final int STR = 1;
    private boolean playingDeathAnimation = false;
    public static final int ENRAGE_PHASE = 2;

    public PunishingBird(final float x, final float y) {
        super(ID, ID, 150, -5.0F, 0, 160.0f, 305.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("PunishingBird/Spriter/SmallBird.scml"));
        setHp(calcAscensionTankiness(150));
        addMove(PECK, Intent.ATTACK_DEBUFF, 3, 3);
        addMove(PUNISHMENT, Intent.ATTACK, calcAscensionSpecial(calcAscensionDamage(50)));
    }

    @Override
    public void usePreBattleAction() {
        atb(new ApplyPowerAction(this, this, new PunishingBirdPunishmentPower(this)));
        AbstractPower punishment = this.getPower(PunishingBirdPunishmentPower.POWER_ID);
        if (punishment instanceof PunishingBirdPunishmentPower && phase == ENRAGE_PHASE) {
            ((PunishingBirdPunishmentPower) punishment).setPunishment(true);
            punishment.updateDescription();
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
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
                if (AbstractDungeon.ascensionLevel >= 17) {
                    applyToTarget(this, this, new StrengthPower(this, STR));
                }
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
        if (phase == ENRAGE_PHASE) {
            setMoveShortcut(PUNISHMENT);
            AbstractPower punishment = this.getPower(PunishingBirdPunishmentPower.POWER_ID);
            if (punishment instanceof PunishingBirdPunishmentPower) {
                ((PunishingBirdPunishmentPower) punishment).setPunishment(false);
                punishment.updateDescription();
            }
            setPhase(DEFAULT_PHASE);
        } else {
            setMoveShortcut(PECK);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case PECK: {
                DetailedIntent detail = new DetailedIntent(this, STATUS, DetailedIntent.WOUND_TEXTURE, DetailedIntent.TargetType.DRAW_PILE);
                detailsList.add(detail);
                if (AbstractDungeon.ascensionLevel >= 17) {
                    DetailedIntent detail2 = new DetailedIntent(this, STR, DetailedIntent.STRENGTH_TEXTURE);
                    detailsList.add(detail2);
                }
                break;
            }
        }
        return detailsList;
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