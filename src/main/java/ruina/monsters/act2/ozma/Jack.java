package ruina.monsters.act2.ozma;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act2.Steal;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Jack extends AbstractRuinaMonster
{
    public static final String ID = makeID(Jack.class.getSimpleName());
    private static final byte ATTACK = 0;

    public final int DRAW_REDUCTION = calcAscensionSpecial(1);

    public Jack(final float x, final float y) {
        super(ID, ID, 100, -5.0F, 0, 135.0f, 160.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Jack/Spriter/Jack.scml"));
        setHp(calcAscensionTankiness(32), calcAscensionTankiness(36));
        addMove(ATTACK, Intent.ATTACK, calcAscensionDamage(5));
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new Steal(this, DRAW_REDUCTION));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case ATTACK: {
                bluntAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(ATTACK);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "WoodStrike", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "WoodStrike", enemy, this);
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "WoodStrike", enemy, this);
    }

}