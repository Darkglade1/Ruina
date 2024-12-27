package ruina.monsters.act1;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act1.Song;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class CrazedEmployee extends AbstractRuinaMonster
{
    public static final String ID = makeID(CrazedEmployee.class.getSimpleName());

    private static final byte TREMBLING_MOTION = 0;
    private static final byte SHAKING_BLOW = 1;

    private final int DEBUFF = calcAscensionSpecial(1);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int SELF_DEBUFF = 1;
    private final int debuff;

    public CrazedEmployee() {
        this(0.0f, 0.0f, 0);
    }

    public CrazedEmployee(final float x, final float y, int debuff) {
        super(ID, ID, 140, 0.0F, 0, 220.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("CrazedEmployee/Spriter/CrazedEmployee.scml"));
        setHp(calcAscensionTankiness(24), calcAscensionTankiness(27));
        addMove(TREMBLING_MOTION, Intent.DEBUFF);
        addMove(SHAKING_BLOW, Intent.ATTACK, calcAscensionDamage(5));
        this.debuff = debuff;
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new Song(this, STRENGTH, SELF_DEBUFF));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case TREMBLING_MOTION: {
                blockAnimation();
                if (debuff == 0) {
                    applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                } else if (debuff == 1) {
                    applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                }
                resetIdle();
                break;
            }
            case SHAKING_BLOW: {
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
        if (debuff == 0) {
            if (firstMove) {
                setMoveShortcut(TREMBLING_MOTION);
            } else {
                setMoveShortcut(SHAKING_BLOW);
            }
        } else if (debuff == 1) {
            if (firstMove) {
                setMoveShortcut(SHAKING_BLOW);
            } else if (moveHistory.size() == 1) {
                setMoveShortcut(TREMBLING_MOTION);
            } else {
                setMoveShortcut(SHAKING_BLOW);
            }
        } else {
            setMoveShortcut(SHAKING_BLOW);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case TREMBLING_MOTION: {
                if (debuff == 0) {
                    DetailedIntent detail = new DetailedIntent(this, DEBUFF, DetailedIntent.WEAK_TEXTURE);
                    detailsList.add(detail);
                } else {
                    DetailedIntent detail2 = new DetailedIntent(this, DEBUFF, DetailedIntent.FRAIL_TEXTURE);
                    detailsList.add(detail2);
                }
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntVert", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

}