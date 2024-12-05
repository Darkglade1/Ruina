package ruina.monsters.act3.heart;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class HeartOfAspiration extends AbstractRuinaMonster
{
    public static final String ID = makeID(HeartOfAspiration.class.getSimpleName());

    private static final byte PULSATION = 0;
    private static final byte BEATS_OF_ASPIRATION = 1;

    private final int STRENGTH = calcAscensionSpecial(2);

    public HeartOfAspiration() {
        this(0.0f, 0.0f);
    }

    public HeartOfAspiration(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 280.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Heart/Spriter/Heart.scml"));
        setHp(calcAscensionTankiness(48), calcAscensionTankiness(55));
        addMove(PULSATION, Intent.BUFF);
        addMove(BEATS_OF_ASPIRATION, Intent.ATTACK, calcAscensionDamage(14));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case PULSATION: {
                specialAnimation();
                for (AbstractMonster mo : monsterList()) {
                    applyToTarget(mo, this, new StrengthPower(mo, STRENGTH));
                }
                resetIdle();
                break;
            }
            case BEATS_OF_ASPIRATION: {
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
        int otherEnemyCount = 0;
        for (AbstractMonster mo : monsterList()) {
            if (mo != this) {
                otherEnemyCount++;
            }
        }
        if (otherEnemyCount > 0) {
            setMoveShortcut(PULSATION);
        } else {
            setMoveShortcut(BEATS_OF_ASPIRATION);
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntBlow", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", null, this);
    }

}