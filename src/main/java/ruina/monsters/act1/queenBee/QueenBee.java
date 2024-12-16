package ruina.monsters.act1.queenBee;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class QueenBee extends AbstractRuinaMonster
{
    public static final String ID = makeID(QueenBee.class.getSimpleName());

    private static final byte BOOST_AGGRESSION = 0;
    private static final byte BOOST_LOYALTY = 1;
    private static final byte HORNET_STRIKE = 2;

    private final int STRENGTH = calcAscensionSpecial(2);
    private final int BLOCK = RuinaMod.getMultiplayerEnemyHealthScaling(calcAscensionTankiness(9));

    public QueenBee() {
        this(0.0f, 0.0f);
    }

    public QueenBee(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 220.0f, 295.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("QueenBee/Spriter/QueenBee.scml"));
        setHp(calcAscensionTankiness(32), calcAscensionTankiness(36));
        addMove(BOOST_AGGRESSION, Intent.BUFF);
        addMove(BOOST_LOYALTY, Intent.DEFEND);
        addMove(HORNET_STRIKE, Intent.ATTACK, calcAscensionDamage(11));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case BOOST_AGGRESSION: {
                specialAnimation();
                boolean foundDrone = false;
                for (AbstractMonster mo : monsterList()) {
                    if (mo instanceof WorkerBee) {
                        applyToTarget(mo, this, new StrengthPower(mo, STRENGTH));
                        foundDrone = true;
                    }
                }
                if (!foundDrone) {
                    applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                }
                resetIdle(1.0f);
                break;
            }
            case BOOST_LOYALTY: {
                specialAnimation();
                boolean foundDrone = false;
                for (AbstractMonster mo : monsterList()) {
                    if (mo instanceof WorkerBee) {
                        block(mo, BLOCK);
                        foundDrone = true;
                    }
                }
                if (!foundDrone) {
                    block(this, BLOCK);
                }
                resetIdle(1.0f);
                break;
            }
            case HORNET_STRIKE: {
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
        if (onlyEnemyAlive()) {
            setMoveShortcut(HORNET_STRIKE);
        } else if (lastMove(BOOST_AGGRESSION)){
            setMoveShortcut(BOOST_LOYALTY);
        } else {
            setMoveShortcut(BOOST_AGGRESSION);
        }
    }

    private boolean onlyEnemyAlive() {
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof WorkerBee) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case BOOST_AGGRESSION: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case BOOST_LOYALTY: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Special", "QueenBeeStab", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "QueenBeeBuff", this);
    }

}