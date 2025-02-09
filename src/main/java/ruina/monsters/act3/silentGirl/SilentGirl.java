package ruina.monsters.act3.silentGirl;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.BetterSpriterAnimation;
import ruina.cards.Guilt;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.NextTurnPowerPower;
import ruina.powers.Paralysis;
import ruina.powers.act3.Remorse;
import ruina.util.DetailedIntent;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SilentGirl extends AbstractRuinaMonster
{
    public static final String ID = makeID(SilentGirl.class.getSimpleName());

    private static final byte DIGGING_NAIL = 0;
    private static final byte SLAM = 1;
    private static final byte A_CRACKED_HEART = 2;
    private static final byte COLLAPSING_HEART = 3;
    private static final byte BROKEN = 4;
    private static final byte LEER = 5;
    private static final byte SUPPRESS = 6;

    private final int FRAIL = calcAscensionSpecial(2);
    private final int VULNERABLE = calcAscensionSpecial(1);
    private final int STRENGTH = calcAscensionSpecial(3);
    private final int BLOCK = calcAscensionTankiness(12);
    private final int PARALYSIS = calcAscensionSpecial(2);
    private final int CURSE_AMT = 1;
    private final int maxHP = calcAscensionTankiness(240);

    public static final int ENRAGE_PHASE = 2;
    private final DummyHammer hammer = new DummyHammer(100.0f, 0.0f);
    private final DummyNail nail = new DummyNail(-300.0f, 0.0f);
    AbstractCard curse = new Guilt();

    public SilentGirl() {
        this(-100.0f, 0.0f);
    }

    public SilentGirl(final float x, final float y) {
        super(ID, ID, 480, 0.0F, 0, 250.0f, 290.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SilentGirl/Spriter/SilentGirl.scml"));
        setHp(maxHP);
        addMove(DIGGING_NAIL, Intent.ATTACK_DEBUFF, calcAscensionDamage(18));
        addMove(SLAM, Intent.ATTACK_DEFEND, calcAscensionDamage(22));
        addMove(A_CRACKED_HEART, Intent.ATTACK_DEBUFF, calcAscensionDamage(17));
        addMove(COLLAPSING_HEART, Intent.ATTACK, calcAscensionDamage(24));
        addMove(BROKEN, Intent.ATTACK, calcAscensionDamage(13), 2);
        addMove(LEER, Intent.DEBUFF);
        addMove(SUPPRESS, Intent.UNKNOWN);

        if (AbstractDungeon.ascensionLevel >= 19) {
            curse.upgrade();
        }
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Story2");
        applyToTarget(this, this, new Remorse(this, CURSE_AMT, curse));
        if (phase == ENRAGE_PHASE) {
            phaseChangeAnimation();
            hammer.deadAnimation();
            nail.deadAnimation();
            resetIdle(1.0f);
            AbstractDungeon.getCurrRoom().cannotLose = false;
        } else {
            applyToTarget(this, this, new UnawakenedPower(this));
            AbstractDungeon.getCurrRoom().cannotLose = true;
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case DIGGING_NAIL: {
                nail.attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new FrailPower(adp(), FRAIL, true));
                nail.resetIdle();
                break;
            }
            case SLAM: {
                block(this, BLOCK);
                hammer.attackAnimation(adp());
                dmg(adp(), info);
                hammer.resetIdle();
                break;
            }
            case A_CRACKED_HEART: {
                nail.attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new VulnerablePower(adp(), VULNERABLE, true));
                nail.resetIdle();
                break;
            }
            case COLLAPSING_HEART: {
                hammer.attackAnimation(adp());
                dmg(adp(), info);
                hammer.resetIdle();
                break;
            }
            case BROKEN: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        specialUpAnimation(adp());
                    } else {
                        specialDownAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case LEER: {
                rangedAnimation(adp());
                applyToTarget(adp(), this, new Paralysis(adp(), PARALYSIS));
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
            case SUPPRESS: {
                phaseChangeAnimation();
                hammer.deadAnimation();
                nail.deadAnimation();
                this.halfDead = false;
                atb(new HealAction(this, this, maxHealth));
                block(this, BLOCK);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                setPhase(ENRAGE_PHASE);
                AbstractDungeon.getCurrRoom().cannotLose = false;
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.halfDead) {
            setMoveShortcut(SUPPRESS);
        } else if (phase == DEFAULT_PHASE) {
            if (lastMove(DIGGING_NAIL)) {
                setMoveShortcut(SLAM);
            } else if (lastMove(SLAM)) {
                setMoveShortcut(A_CRACKED_HEART);
            } else if (lastMove(A_CRACKED_HEART)) {
                setMoveShortcut(COLLAPSING_HEART);
            } else {
                setMoveShortcut(DIGGING_NAIL);
            }
        } else {
            if (lastMove(BROKEN)) {
                setMoveShortcut(LEER);
            } else {
                setMoveShortcut(BROKEN);
            }
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case DIGGING_NAIL: {
                DetailedIntent detail = new DetailedIntent(this, FRAIL, DetailedIntent.FRAIL_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case SLAM: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case A_CRACKED_HEART: {
                DetailedIntent detail = new DetailedIntent(this, VULNERABLE, DetailedIntent.VULNERABLE_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case LEER: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, PARALYSIS, DetailedIntent.PARALYSIS_TEXTURE);
                detailsList.add(detail2);
                break;
            }
            case SUPPRESS: {
                DetailedIntent detail = new DetailedIntent(this, this.maxHP, DetailedIntent.HEAL_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail2);
                DetailedIntent detail3 = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail3);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + phase);
                this.isDone = true;
            }
        });
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        hammer.render(sb);
        nail.render(sb);
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        tips.add(new CardPowerTip(curse.makeStatEquivalentCopy()));
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead && AbstractDungeon.getCurrRoom().cannotLose) {
            this.halfDead = true;
            for (AbstractPower p : this.powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            rollMove();
            createIntent();
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power.ID.equals(Remorse.POWER_ID)) && !(power.ID.equals(StrengthPower.POWER_ID)) && !(power instanceof NextTurnPowerPower)) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            super.die(triggerRelics);
            onBossVictoryLogic();
        }
        if (this.maxHealth <= 0) {
            setMaxHP();
            AbstractDungeon.actionManager.addToBottom(new InstantKillAction(this));
        }
    }

    private void setMaxHP() {
        this.maxHealth = maxHP;
        healthBarUpdatedEvent();
    }

    private void rangedAnimation(AbstractCreature enemy) {
        animationAction("Ranged", "SilentEye", enemy, this);
    }

    private void phaseChangeAnimation() {
        animationAction("Ranged", "SilentPhaseChange", this);
    }

    private void specialUpAnimation(AbstractCreature enemy) {
        animationAction("SpecialUp", "SilentHammer", enemy, this);
    }

    private void specialDownAnimation(AbstractCreature enemy) {
        animationAction("SpecialDown", "SilentHammer", enemy, this);
    }

}