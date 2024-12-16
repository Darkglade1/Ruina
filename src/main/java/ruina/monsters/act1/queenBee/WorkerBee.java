package ruina.monsters.act1.queenBee;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class WorkerBee extends AbstractRuinaMonster
{
    public static final String ID = makeID(WorkerBee.class.getSimpleName());

    private static final byte CARRY_LARVAE = 0;
    private static final byte GARDU_DU_CORPS = 1;

    private final int BLOCK = calcAscensionTankiness(5);

    public WorkerBee() {
        this(0.0f, 0.0f);
    }

    public WorkerBee(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 230.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("WorkerBee/Spriter/WorkerBee.scml"));
        setHp(calcAscensionTankiness(42), calcAscensionTankiness(46));
        addMove(CARRY_LARVAE, Intent.ATTACK_DEFEND, calcAscensionDamage(9));
        addMove(GARDU_DU_CORPS, Intent.ATTACK, calcAscensionDamage(5), 2);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case CARRY_LARVAE: {
                block(this, BLOCK);
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case GARDU_DU_CORPS: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(adp());
                    dmg(adp(), info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(CARRY_LARVAE)) {
            setMoveShortcut(GARDU_DU_CORPS);
        } else {
            setMoveShortcut(CARRY_LARVAE);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case CARRY_LARVAE: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "QueenBeeLegAtk", enemy, this);
    }

}