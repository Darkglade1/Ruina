package ruina.monsters.act2;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.DetailedIntent;
import ruina.util.Wiz;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class AWolf extends AbstractRuinaMonster
{
    public static final String ID = makeID(AWolf.class.getSimpleName());

    private static final byte SWIPE = 0;
    private static final byte PACK_TACTICS = 1;
    private static final byte CRIPPLING_STRIKE = 2;

    private final int BLOCK = calcAscensionTankiness(7);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int DEBUFF = calcAscensionSpecial(1);

    public AWolf() {
        this(0.0f, 0.0f);
    }

    public AWolf(final float x, final float y) {
        super(ID, ID, 40, -5.0F, 0, 230.0f, 365.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("AWolf/Spriter/AWolf.scml"));
        setHp(calcAscensionTankiness(40), calcAscensionTankiness(43));
        addMove(SWIPE, Intent.ATTACK_DEFEND, calcAscensionDamage(8));
        addMove(PACK_TACTICS, Intent.BUFF);
        addMove(CRIPPLING_STRIKE, Intent.ATTACK_DEBUFF, calcAscensionDamage(6));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case SWIPE: {
                attackAnimation(adp());
                block(this, BLOCK);
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case PACK_TACTICS: {
                blockAnimation();
                for (AbstractMonster mo : Wiz.monsterList()) {
                    applyToTarget(mo, this, new StrengthPower(mo, STRENGTH));
                }
                resetIdle();
                break;
            }
            case CRIPPLING_STRIKE: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new VulnerablePower(adp(), DEBUFF, true));
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(SWIPE)) {
            setMoveShortcut(PACK_TACTICS);
        } else if (lastMove(PACK_TACTICS)) {
            setMoveShortcut(CRIPPLING_STRIKE);
        } else {
            setMoveShortcut(SWIPE);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case SWIPE: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case PACK_TACTICS: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE, DetailedIntent.TargetType.ALL_ENEMIES);
                detailsList.add(detail);
                break;
            }
            case CRIPPLING_STRIKE: {
                DetailedIntent detail = new DetailedIntent(this, DEBUFF, DetailedIntent.VULNERABLE_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "Claw", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

}