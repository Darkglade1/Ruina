package ruina.monsters.act1.redShoes;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Bleed;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class LeftShoe extends AbstractRuinaMonster
{
    public static final String ID = makeID(LeftShoe.class.getSimpleName());

    private static final byte SANGUINE_DESIRE = 0;

    private final int BLEED = calcAscensionSpecial(1);

    public LeftShoe() {
        this(0.0f, 0.0f);
    }

    public LeftShoe(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 220.0f, 285.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("LeftShoe/Spriter/LeftShoe.scml"));
        setHp(calcAscensionTankiness(22), calcAscensionTankiness(24));
        addMove(SANGUINE_DESIRE, Intent.ATTACK_DEBUFF, calcAscensionDamage(6));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case SANGUINE_DESIRE: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Bleed(adp(), BLEED));
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(SANGUINE_DESIRE, MOVES[SANGUINE_DESIRE]);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "ShoesAtk", enemy, this);
    }

}