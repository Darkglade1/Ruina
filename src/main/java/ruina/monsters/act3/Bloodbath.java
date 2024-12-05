package ruina.monsters.act3;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Bleed;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Bloodbath extends AbstractRuinaMonster
{
    public static final String ID = makeID(Bloodbath.class.getSimpleName());

    private static final byte PALE_HANDS = 0;
    private static final byte DEPRESSION = 1;

    private final int BLOCK = calcAscensionTankiness(7);
    private final int DEBUFF = calcAscensionSpecial(1);
    private final int BLEED = calcAscensionSpecial(3);

    public Bloodbath() {
        this(0.0f, 0.0f);
    }

    public Bloodbath(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 220.0f, 320.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Bloodbath/Spriter/Bloodbath.scml"));
        setHp(calcAscensionTankiness(88), calcAscensionTankiness(94));
        addMove(PALE_HANDS, Intent.ATTACK_DEBUFF, calcAscensionDamage(15));
        addMove(DEPRESSION, Intent.DEFEND_DEBUFF);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case PALE_HANDS: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Bleed(adp(), BLEED));
                resetIdle();
                break;
            }
            case DEPRESSION: {
                specialAnimation();
                block(this, BLOCK);
                applyToTarget(adp(), this, new StrengthPower(adp(), -DEBUFF));
                applyToTarget(adp(), this, new DexterityPower(adp(), -DEBUFF));
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(PALE_HANDS)) {
            setMoveShortcut(DEPRESSION);
        } else {
            setMoveShortcut(PALE_HANDS);
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BloodAttack", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "BloodSpecial", this);
    }

}