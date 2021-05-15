package ruina.monsters.act3.silentGirl;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
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
    private final int VULNERABLE = calcAscensionSpecial(1);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int BLOCK = calcAscensionTankiness(12);
    private final int PARALYSIS = calcAscensionSpecial(2);
    private final int POWER_STRENGTH = calcAscensionSpecial(1);
    private final int POWER_METALLICIZE = calcAscensionSpecial(2);
    private final float HP_THRESHOLD = 0.5f;
    private int enraged = 1; //1 is false, 2 is true

    private DummyHammer hammer = new DummyHammer(100.0f, 0.0f);
    private DummyNail nail = new DummyNail(-300.0f, 0.0f);

    public static final String POWER_ID = makeID("HammerAndNail");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SilentGirl() {
        this(-100.0f, 0.0f);
    }

    public SilentGirl(final float x, final float y) {
        super(NAME, ID, 450, 0.0F, 0, 250.0f, 290.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SilentGirl/Spriter/SilentGirl.scml"));
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(DIGGING_NAIL, Intent.ATTACK_DEBUFF, calcAscensionDamage(14));
        addMove(SLAM, Intent.ATTACK_DEFEND, calcAscensionDamage(18));
        addMove(A_CRACKED_HEART, Intent.ATTACK_DEBUFF, calcAscensionDamage(13));
        addMove(COLLAPSING_HEART, Intent.ATTACK, calcAscensionDamage(20));
        addMove(BROKEN, Intent.ATTACK, calcAscensionDamage(11), 2, true);
        addMove(LEER, Intent.DEBUFF);
        addMove(SUPPRESS, Intent.DEFEND_BUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Story2");
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void onUseCard(AbstractCard card, UseCardAction action) {
                if (GameActionManager.turn % 2 == 0 && card.costForTurn >= 0 && card.costForTurn % 2 == 0) {
                    this.flash();
                    applyToTarget(owner, owner, new MetallicizePower(owner, POWER_METALLICIZE));
                }
                if (GameActionManager.turn % 2 == 1 && card.costForTurn >= 0 && card.costForTurn % 2 == 1) {
                    this.flash();
                    applyToTarget(owner, owner, new StrengthPower(owner, POWER_STRENGTH));
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + POWER_STRENGTH + POWER_DESCRIPTIONS[1] + POWER_METALLICIZE + POWER_DESCRIPTIONS[2];
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
                    specialUpAnimation(adp());
                    waitAnimation(0.25f);
                    specialDownAnimation(adp());
                    dmg(adp(), info);
                    waitAnimation();
                }
                resetIdle(0.0f);
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
                makePowerRemovable(this, POWER_ID);
                atb(new RemoveSpecificPowerAction(this, this, POWER_ID));
                applyToTarget(this, this, new BarricadePower(this));
                block(this, BLOCK);
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

    private void rangedAnimation(AbstractCreature enemy) {
        animationAction("Ranged", "SilentEye", enemy, this);
    }

    private void phaseChangeAnimation() {
        animationAction("Ranged", "SilentPhaseChange", this);
    }

    private void specialUpAnimation(AbstractCreature enemy) {
        animationAction("SpecialUp", null, enemy, this);
    }

    private void specialDownAnimation(AbstractCreature enemy) {
        animationAction("SpecialDown", "SilentHammer", enemy, this);
    }

}