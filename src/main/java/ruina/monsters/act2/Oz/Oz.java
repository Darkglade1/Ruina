package ruina.monsters.act2.Oz;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.act2.ozma.Jack;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;
import ruina.vfx.OzCrystalEffect;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Oz extends AbstractRuinaMonster
{
    public static final String ID = makeID(Oz.class.getSimpleName());

    private static final byte WELCOME = 0;
    private static final byte BEHAVE = 1;
    private static final byte ARISE = 2;
    private static final byte NOISY = 3;
    private static final byte LIGHT = 4;
    private static final byte AWAY = 5;

    private final int STRENGTH = calcAscensionSpecial(2);
    private final int WEAK = calcAscensionSpecial(2);
    private final int BLOCK = calcAscensionTankiness(10);
    private final int HEAL = calcAscensionTankiness(10);
    private final int FRAGILE = calcAscensionSpecial(2);

    public Oz() {
        this(150.0f, 0.0f);
    }

    public Oz(final float x, final float y) {
        super(ID, ID, 500, -5.0F, 0, 230.0f, 450.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Oz/Spriter/Oz.scml"));
        setHp(calcAscensionTankiness(280));
        addMove(WELCOME, Intent.ATTACK, calcAscensionDamage(20));
        addMove(BEHAVE, Intent.ATTACK, calcAscensionDamage(7), 2);
        addMove(ARISE, Intent.UNKNOWN);
        addMove(NOISY, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
        addMove(LIGHT, Intent.DEFEND_BUFF);
        addMove(AWAY, Intent.ATTACK_DEBUFF, calcAscensionDamage(24));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Roland3");
        //applyToTarget(this, this, new Agony(this));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case WELCOME: {
                specialStartAnimation();
                OzCrystalEffect();
                dmg(adp(), info);
                resetIdle(1.0f);
                break;
            }
            case BEHAVE: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation2(adp());
                    dmg(adp(), info);
                    hitEffect(adp());
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
            case ARISE: {
                buffAnimation();
                Summon();
                resetIdle(1.0f);
                break;
            }
            case NOISY: {
                attackAnimation(adp());
                dmg(adp(), info);
                hitEffect(adp());
                applyToTarget(adp(), this, new WeakPower(adp(), WEAK, true));
                resetIdle(1.0f);
                break;
            }
            case LIGHT: {
                buffAnimation();
                block(this, BLOCK);
                atb(new HealAction(this, this, HEAL));
                resetIdle(1.0f);
                break;
            }
            case AWAY: {
                specialStartAnimation();
                OzCrystalEffect();
                dmg(adp(), info);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    public void Summon() {
        AbstractMonster minion = new ScowlingFace(-450.0F, 0.0f, true);
        atb(new SpawnMonsterAction(minion, true));
        atb(new UsePreBattleActionAction(minion));
        AbstractMonster minion2 = new ScowlingFace(-150F, 0.0f, false);
        atb(new SpawnMonsterAction(minion2, true));
        atb(new UsePreBattleActionAction(minion2));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(WELCOME);
        } else if (!areMinionsAlive()) {
            setMoveShortcut(ARISE);
        } else if (moveHistory.size() >= 4 && !this.lastMove(AWAY) && !this.lastMoveBefore(AWAY) && !this.lastMoveBeforeBefore(AWAY)) {
            setMoveShortcut(AWAY);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(NOISY) && !this.lastMoveBefore(NOISY)) {
                possibilities.add(NOISY);
            }
            if (!this.lastMove(LIGHT) && !this.lastMoveBefore(LIGHT)) {
                possibilities.add(LIGHT);
            }
            if (!this.lastMove(BEHAVE) && !this.lastMoveBefore(BEHAVE)) {
                possibilities.add(BEHAVE);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    private boolean areMinionsAlive() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof ScowlingFace && !mo.isDeadOrEscaped()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case ARISE: {
                DetailedIntent detail = new DetailedIntent(this, DetailedIntent.SUMMON);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof ScowlingFace) {
                atb(new SuicideAction(mo));
            }
        }
        onBossVictoryLogic();
    }

    private void hitEffect(AbstractCreature target) {
        Texture texture = TexLoader.getTexture(RuinaMod.makeVfxPath("OzHitEffect.png"));
        float duration = 0.6f;
        AbstractGameEffect appear = new VfxBuilder(texture, target.hb.cX, target.hb.cY, duration)
                .fadeOut(duration)
                .build();
        atb(new VFXAction(appear, duration));
    }

    private void OzCrystalEffect() {
        Texture texture = TexLoader.getTexture(RuinaMod.makeVfxPath("OzCrystalFall.png"));
        float duration = 1.5f;
        AbstractGameEffect effect = new VfxBuilder(texture, adp().hb.cX, 0f, duration)
                .moveY(Settings.HEIGHT - (100.0f * Settings.scale), Settings.HEIGHT, VfxBuilder.Interpolations.SWING)
                .andThen(0.5f)
                .moveY(Settings.HEIGHT, adp().hb.y + adp().hb.height / 6, VfxBuilder.Interpolations.EXP5IN)
                .build();
        atb(new VFXAction(effect, duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound("OzStrongAtkDown");
                this.isDone = true;
            }
        });
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Ranged", "OzAtkBoom", enemy, this);
    }

    private void attackAnimation2(AbstractCreature enemy) {
        animationAction("Ranged", "OzAtkUp", enemy, this);
    }

    private void specialStartAnimation() {
        animationAction("Special", "OzStrongAtkStart", this);
    }

    private void buffAnimation() {
        animationAction("Ranged", "OzMagic", this);
    }

}