package ruina.monsters.act3;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class EyeballChick extends AbstractRuinaMonster
{
    public static final String ID = makeID(EyeballChick.class.getSimpleName());

    private static final byte STARE = 0;
    private static final byte PIERCE = 1;

    private final int DEBUFF = calcAscensionSpecial(1);
    private final int STRENGTH = calcAscensionSpecial(1);

    public EyeballChick() {
        this(0.0f, 0.0f);
    }

    public EyeballChick(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0.0f, 200.0f, 220.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("EyeballChick/Spriter/EyeballChick.scml"));
        setHp(calcAscensionTankiness(40), calcAscensionTankiness(46));
        addMove(STARE, Intent.ATTACK_DEBUFF, calcAscensionDamage(8));
        addMove(PIERCE, Intent.ATTACK_BUFF, calcAscensionDamage(7));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case STARE: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                resetIdle();
                break;
            }
            case PIERCE: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastTwoMoves(STARE)) {
            possibilities.add(STARE);
        }
        if (!this.lastTwoMoves(PIERCE)) {
            possibilities.add(PIERCE);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move);
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case STARE: {
                DetailedIntent detail = new DetailedIntent(this, DEBUFF, DetailedIntent.FRAIL_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case PIERCE: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "WoodFinish", enemy, this);
    }

}