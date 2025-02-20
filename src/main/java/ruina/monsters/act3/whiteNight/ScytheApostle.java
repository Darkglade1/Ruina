package ruina.monsters.act3.whiteNight;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class ScytheApostle extends AbstractRuinaMonster {
    public static final String ID = makeID(ScytheApostle.class.getSimpleName());

    private static final byte FOLLOW_THEE = 0;
    private static final byte THY_WILL_BE_DONE = 1;

    private final int BLOCK = calcAscensionTankiness(9);

    public ScytheApostle(final float x, final float y) {
        super(ID, ID, 75, -5.0F, 0, 160.0f, 185.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ScytheApostle/Spriter/ScytheApostle.scml"));
        setHp(calcAscensionTankiness(63), calcAscensionTankiness(67));
        addMove(FOLLOW_THEE, Intent.ATTACK_DEFEND, calcAscensionDamage(12));
        addMove(THY_WILL_BE_DONE, Intent.ATTACK, calcAscensionDamage(7), 2);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void takeTurn() {
       super.takeTurn();
        switch (nextMove) {
            case THY_WILL_BE_DONE:
                for (int i = 0; i < multiplier; i++) {
                    if (i == 0) {
                        slashUpAnimation(adp());
                    } else {
                        slashDownAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            case FOLLOW_THEE:
                slashDownAnimation(adp());
                block(this, BLOCK);
                dmg(adp(), info);
                resetIdle();
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(FOLLOW_THEE)) {
            setMoveShortcut(THY_WILL_BE_DONE);
        } else {
            setMoveShortcut(FOLLOW_THEE);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case FOLLOW_THEE: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void slashUpAnimation(AbstractCreature enemy) {
        animationAction("SlashUp", "ApostleScytheUp", enemy, this);
    }

    private void slashDownAnimation(AbstractCreature enemy) {
        animationAction("SlashDown", "ApostleScytheDown", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Block", null, this);
    }

}