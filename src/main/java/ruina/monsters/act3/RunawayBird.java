package ruina.monsters.act3;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class RunawayBird extends AbstractRuinaMonster
{
    public static final String ID = makeID(RunawayBird.class.getSimpleName());

    private static final byte SWEEP = 0;
    private static final byte SHRIEK = 1;

    private final int STATUS = calcAscensionSpecial(2);

    public RunawayBird() {
        this(0.0f, 0.0f);
    }

    public RunawayBird(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0.0f, 200.0f, 220.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("RunawayBird/Spriter/RunawayBird.scml"));
        setHp(calcAscensionTankiness(36), calcAscensionTankiness(42));
        addMove(SWEEP, Intent.ATTACK, calcAscensionDamage(10));
        addMove(SHRIEK, Intent.DEBUFF);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case SWEEP: {
                sweepAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case SHRIEK: {
                shriekAnimation(adp());
                intoDrawMo(new Dazed(), STATUS, this);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastTwoMoves(SWEEP)) {
            possibilities.add(SWEEP);
        }
        if (!this.lastTwoMoves(SHRIEK)) {
            possibilities.add(SHRIEK);
        }
        byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
        setMoveShortcut(move);
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case SHRIEK: {
                DetailedIntent detail = new DetailedIntent(this, STATUS, DetailedIntent.DAZED_TEXTURE, DetailedIntent.TargetType.DRAW_PILE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void sweepAnimation(AbstractCreature enemy) {
        animationAction("Sweep", "BirdSweep", enemy, this);
    }

    private void shriekAnimation(AbstractCreature enemy) {
        animationAction("Shriek", "BirdShout", enemy, this);
    }

}