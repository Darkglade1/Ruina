package ruina.monsters.act3.heart;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

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

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case PULSATION: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE, DetailedIntent.TargetType.ALL_ENEMIES);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntBlow", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", null, this);
    }

}