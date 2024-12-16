package ruina.monsters.act1;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act1.Affection;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class TeddyBear extends AbstractRuinaMonster
{
    public static final String ID = makeID(TeddyBear.class.getSimpleName());

    private static final byte TIMID_ENDEARMENT = 0;
    private static final byte DISPLAY_AFFECTION = 1;

    private final int BLOCK = RuinaMod.getMultiplayerEnemyHealthScaling(calcAscensionTankiness(11));
    private final int STRENGTH = calcAscensionSpecial(1);

    public TeddyBear() {
        this(0.0f, 0.0f);
    }

    public TeddyBear(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 250.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("TeddyBear/Spriter/TeddyBear.scml"));
        setHp(calcAscensionTankiness(55), calcAscensionTankiness(59));
        addMove(TIMID_ENDEARMENT, Intent.DEFEND);
        addMove(DISPLAY_AFFECTION, Intent.ATTACK, calcAscensionDamage(12));
    }

    @Override
    public void usePreBattleAction() {
        playSound("TeddyOn", 2.0f);
        applyToTarget(this, this, new Affection(this, STRENGTH));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case TIMID_ENDEARMENT: {
                blockAnimation();
                block(this, BLOCK);
                resetIdle();
                break;
            }
            case DISPLAY_AFFECTION: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(TIMID_ENDEARMENT)) {
            setMoveShortcut(DISPLAY_AFFECTION);
        } else {
            setMoveShortcut(TIMID_ENDEARMENT);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case TIMID_ENDEARMENT: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "TeddyAtk", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", "TeddyBlock", this);
    }

}