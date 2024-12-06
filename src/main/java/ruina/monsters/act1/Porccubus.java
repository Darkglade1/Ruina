package ruina.monsters.act1;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.ThornsPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act1.Pleasure;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Porccubus extends AbstractRuinaMonster
{
    public static final String ID = makeID(Porccubus.class.getSimpleName());

    private static final byte UNBEARABLE_PLEASURE = 0;
    private static final byte BRISTLE = 1;

    private final int THORNS = calcAscensionSpecial(1);
    private final int PLEASURE_DAMAGE = calcAscensionSpecial(3);

    public Porccubus() {
        this(0.0f, 0.0f);
    }

    public Porccubus(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 250.0f, 285.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Porccubus/Spriter/Porccubus.scml"));
        setHp(calcAscensionTankiness(45), calcAscensionTankiness(48));
        addMove(UNBEARABLE_PLEASURE, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
        addMove(BRISTLE, Intent.ATTACK_BUFF, calcAscensionDamage(8));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case UNBEARABLE_PLEASURE: {
                pierceAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Pleasure(adp(), PLEASURE_DAMAGE, 1));
                resetIdle();
                break;
            }
            case BRISTLE: {
                bluntAnimation(adp());
                dmg(adp(), info);
                applyToTarget(this, this, new ThornsPower(this, THORNS));
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(UNBEARABLE_PLEASURE)) {
            setMoveShortcut(BRISTLE);
        } else {
            setMoveShortcut(UNBEARABLE_PLEASURE);
        }
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "PorccuPenetrate", enemy, this);
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "PorccuStrongStab2", enemy, this);
    }

}