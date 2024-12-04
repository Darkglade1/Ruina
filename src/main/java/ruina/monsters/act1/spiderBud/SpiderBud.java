package ruina.monsters.act1.spiderBud;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.EntanglePower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SpiderBud extends AbstractRuinaMonster
{
    public static final String ID = makeID(SpiderBud.class.getSimpleName());

    private static final byte PROTECTIVE_INSTINCTS = 0;
    private static final byte COCOON = 1;
    private static final byte CATCHING_FOOD = 2;

    private boolean enraged = false;
    private int currentMove = PROTECTIVE_INSTINCTS;

    private final int BLOCK = calcAscensionTankiness(4);

    public static final String POWER_ID = makeID("Hunt");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SpiderBud() {
        this(0.0f, 0.0f);
    }

    public SpiderBud(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 200.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SpiderBud/Spriter/SpiderBud.scml"));
        setHp(calcAscensionTankiness(35), calcAscensionTankiness(38));
        addMove(PROTECTIVE_INSTINCTS, Intent.DEFEND);
        addMove(COCOON, Intent.STRONG_DEBUFF);
        addMove(CATCHING_FOOD, Intent.ATTACK, calcAscensionDamage(15));
    }

    @Override
    public void usePreBattleAction() {
        playSound("SpiderDown");
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void onSpecificTrigger() {
                if (!enraged) {
                    flash();
                    enraged = true;
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case PROTECTIVE_INSTINCTS: {
                specialAnimation();
                for (AbstractMonster mo : monsterList()) {
                    block(mo, BLOCK);
                }
                resetIdle();
                currentMove = COCOON;
                break;
            }
            case COCOON: {
                debuffAnimation(adp());
                applyToTarget(adp(), this, new EntanglePower(adp()));
                resetIdle();
                currentMove = PROTECTIVE_INSTINCTS;
                break;
            }
            case CATCHING_FOOD: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (enraged || onlyEnemyAlive()) {
            setMoveShortcut(CATCHING_FOOD, MOVES[CATCHING_FOOD]);
            enraged = false;
        } else {
            setMoveShortcut((byte) currentMove, MOVES[currentMove]);
        }
    }

    private boolean onlyEnemyAlive() {
        for (AbstractMonster mo : monsterList()) {
            if (mo != this) {
                return false;
            }
        }
        return true;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "SpiderStrongAtk", enemy, this);
    }

    private void debuffAnimation(AbstractCreature enemy) {
        animationAction("Attack", null, enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "SpiderProtect", 2.0f, this);
    }

}