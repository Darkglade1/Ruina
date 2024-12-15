package ruina.monsters.act1.spiderBud;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.EntanglePower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act1.Hunt;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SpiderBud extends AbstractRuinaMonster
{
    public static final String ID = makeID(SpiderBud.class.getSimpleName());

    private static final byte PROTECTIVE_INSTINCTS = 0;
    private static final byte COCOON = 1;
    private static final byte CATCHING_FOOD = 2;

    public boolean enraged = false;

    private final int BLOCK = calcAscensionTankiness(4);

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
        applyToTarget(this, this, new Hunt(this));
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
                break;
            }
            case COCOON: {
                debuffAnimation(adp());
                applyToTarget(adp(), this, new EntanglePower(adp()));
                resetIdle();
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
            setMoveShortcut(CATCHING_FOOD);
            enraged = false;
        } else if (lastMoveIgnoringMove(PROTECTIVE_INSTINCTS, CATCHING_FOOD)){
            setMoveShortcut(COCOON);
        } else {
            setMoveShortcut(PROTECTIVE_INSTINCTS);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case PROTECTIVE_INSTINCTS: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE, DetailedIntent.TargetType.ALL_ENEMIES);
                detailsList.add(detail);
                break;
            }
            case COCOON: {
                DetailedIntent detail = new DetailedIntent(this, 1, DetailedIntent.WEB_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
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