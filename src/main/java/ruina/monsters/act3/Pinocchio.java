package ruina.monsters.act3;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.cards.Lie;
import ruina.cards.LyingIsBad;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Pinocchio extends AbstractRuinaMonster
{
    public static final String ID = makeID(Pinocchio.class.getSimpleName());

    private static final byte LEARN = 0;
    private static final byte LIE = 1;
    private static final byte FIB = 2;

    public final int ARTIFACT = calcAscensionSpecial(2);
    public final int BLOCK = calcAscensionTankiness(20);
    public final int STRENGTH = calcAscensionSpecial(2);
    public final int STATUS = calcAscensionSpecial(2);
    public final int INITIAL_STATUS = calcAscensionSpecial(3);
    public final int DEBUFF = calcAscensionSpecial(1);

    public Pinocchio() {
        this(0.0f, 0.0f);
    }

    public Pinocchio(final float x, final float y) {
        super(ID, ID, 170, -5.0F, 0, 250.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Pinocchio/Spriter/Pinocchio.scml"));
        this.setHp(calcAscensionTankiness(190));

        addMove(LEARN, Intent.ATTACK, calcAscensionDamage(10), 2);
        addMove(LIE, Intent.DEBUFF);
        addMove(FIB, Intent.DEFEND_DEBUFF);
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new ArtifactPower(this, ARTIFACT));
        intoDrawMo(new Lie(), INITIAL_STATUS, this);
        makeInHand(new LyingIsBad());
        playSound("PinoOn");
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case LEARN: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(adp());
                    dmg(adp(), info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
            case LIE: {
                blockAnimation();
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                intoDrawMo(new Lie(), STATUS, this);
                resetIdle();
                break;
            }
            case FIB: {
                blockAnimation();
                block(this, BLOCK);
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntBlow", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", "PinoLie", this);
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMoveBefore(LIE)) {
            setMoveShortcut(FIB);
        } else if (this.lastMove(LEARN)) {
            setMoveShortcut(LIE);
        } else {
            setMoveShortcut(LEARN);
        }
    }
}