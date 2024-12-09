package ruina.monsters.act3.whiteNight;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SpearApostle extends AbstractRuinaMonster {
    public static final String ID = makeID(SpearApostle.class.getSimpleName());

    private static final byte FOR_HE_IS_HOLY = 0;
    private static final byte THE_WILL_OF_THE_LORD_BE_DONE = 1;

    private final int STATUS = calcAscensionSpecial(1);

    private final WhiteNight whiteNight;

    public SpearApostle(final float x, final float y, WhiteNight whiteNight) {
        super(ID, ID, 50, -5.0F, 0, 160.0f, 185.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SpearApostle/Spriter/SpearApostle.scml"));
        setHp(calcAscensionTankiness(45), calcAscensionTankiness(50));
        addMove(THE_WILL_OF_THE_LORD_BE_DONE, Intent.ATTACK, calcAscensionDamage(20));
        addMove(FOR_HE_IS_HOLY, Intent.ATTACK_DEBUFF, calcAscensionDamage(10));
        this.whiteNight = whiteNight;
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
            case THE_WILL_OF_THE_LORD_BE_DONE:
                spearAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            case FOR_HE_IS_HOLY:
                spearAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                intoDrawMo(new Wound(), STATUS, this);
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(THE_WILL_OF_THE_LORD_BE_DONE)) {
            setMoveShortcut(FOR_HE_IS_HOLY);
        } else {
            setMoveShortcut(THE_WILL_OF_THE_LORD_BE_DONE);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case FOR_HE_IS_HOLY: {
                DetailedIntent detail = new DetailedIntent(this, STATUS, DetailedIntent.WOUND_TEXTURE, DetailedIntent.TargetType.DRAW_PILE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void spearAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "ApostleSpear", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Block", null, this);
    }


    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (int i = 0; i < whiteNight.minions.length; i++) {
            if (whiteNight.minions[i] == this) {
                whiteNight.minions[i] = null;
                break;
            }
        }
    }
}