package ruina.monsters.act3.silentGirl;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.cards.Guilt;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Paralysis;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SilentGirl extends AbstractRuinaMonster
{
    public static final String ID = makeID(SilentGirl.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte DIGGING_NAIL = 0;
    private static final byte SLAM = 1;
    private static final byte A_CRACKED_HEART = 2;
    private static final byte COLLAPSING_HEART = 3;
    private static final byte BROKEN = 4;
    private static final byte LEER = 5;
    private static final byte SUPPRESS = 6;

    private final int FRAIL = calcAscensionSpecial(2);
    private final int VULNERABLE = calcAscensionSpecial(2);
    private final int STRENGTH = calcAscensionSpecial(3);
    private final int BLOCK = calcAscensionTankiness(12);
    private final int PARALYSIS = calcAscensionSpecial(2);
    private final int CURSE_AMT = 1;
    private final float HP_THRESHOLD = 0.5f;
    private int enraged = 1; //1 is false, 2 is true

    private DummyHammer hammer = new DummyHammer(100.0f, 0.0f);
    private DummyNail nail = new DummyNail(-300.0f, 0.0f);
    AbstractCard curse = new Guilt();

    public static final String POWER_ID = makeID("Remorse");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SilentGirl() {
        this(-100.0f, 0.0f);
    }

    public SilentGirl(final float x, final float y) {
        super(NAME, ID, 480, 0.0F, 0, 250.0f, 290.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SilentGirl/Spriter/SilentGirl.scml"));
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(DIGGING_NAIL, Intent.ATTACK_DEBUFF, calcAscensionDamage(16));
        addMove(SLAM, Intent.ATTACK_DEFEND, calcAscensionDamage(20));
        addMove(A_CRACKED_HEART, Intent.ATTACK_DEBUFF, calcAscensionDamage(15));
        addMove(COLLAPSING_HEART, Intent.ATTACK, calcAscensionDamage(22));
        addMove(BROKEN, Intent.ATTACK, calcAscensionDamage(12), 2, true);
        addMove(LEER, Intent.DEBUFF);
        addMove(SUPPRESS, Intent.DEFEND_BUFF);

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
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, CURSE_AMT) {
            @Override
            public void atEndOfRound() {
                atb(new MakeTempCardInDrawPileAction(curse.makeStatEquivalentCopy(), amount, false, true));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + CURSE_AMT + POWER_DESCRIPTIONS[1] + FontHelper.colorString(curse.name, "y") + POWER_DESCRIPTIONS[2];
            }
        });
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

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
                block(this, BLOCK * 3);
                atb(new RemoveDebuffsAction(this));
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                enraged = 2;
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (enraged == 1) {
            if (currentHealth <= maxHealth * HP_THRESHOLD) {
                setMoveShortcut(SUPPRESS, MOVES[SUPPRESS]);
            } else if (lastMove(DIGGING_NAIL)) {
                setMoveShortcut(SLAM, MOVES[SLAM]);
            } else if (lastMove(SLAM)) {
                setMoveShortcut(A_CRACKED_HEART, MOVES[A_CRACKED_HEART]);
            } else if (lastMove(A_CRACKED_HEART)) {
                setMoveShortcut(COLLAPSING_HEART, MOVES[COLLAPSING_HEART]);
            } else {
                setMoveShortcut(DIGGING_NAIL, MOVES[DIGGING_NAIL]);
            }
        } else {
            if (lastMove(BROKEN)) {
                setMoveShortcut(LEER, MOVES[LEER]);
            } else {
                setMoveShortcut(BROKEN, MOVES[BROKEN]);
            }
        }
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + enraged);
                this.isDone = true;
            }
        });
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (!this.isDeadOrEscaped()) {
            hammer.render(sb);
            nail.render(sb);
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        tips.add(new CardPowerTip(curse.makeStatEquivalentCopy()));
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        onBossVictoryLogic();
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