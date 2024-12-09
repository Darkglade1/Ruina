package ruina.monsters.act1;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Paralysis;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Butterflies extends AbstractRuinaMonster
{
    public static final String ID = makeID(Butterflies.class.getSimpleName());

    private static final byte TRANQUILITY = 0;
    private static final byte LIBERATION = 1;

    private final int STATUS = calcAscensionSpecial(1);
    private final int PARALYSIS = calcAscensionSpecial(1);

    public Butterflies() {
        this(0.0f, 0.0f);
    }

    public Butterflies(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 100.0f, 160.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Butterflies/Spriter/Butterflies.scml"));
        setHp(calcAscensionTankiness(12), calcAscensionTankiness(15));
        addMove(TRANQUILITY, Intent.ATTACK, 5);
        addMove(LIBERATION, Intent.DEBUFF);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case TRANQUILITY: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case LIBERATION: {
                specialAnimation(adp());
                applyToTarget(adp(), this, new Paralysis(adp(), PARALYSIS));
                intoDiscardMo(new Dazed(), STATUS, this);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastTwoMoves(TRANQUILITY)) {
            possibilities.add(TRANQUILITY);
        }
        if (!this.lastTwoMoves(LIBERATION)) {
            possibilities.add(LIBERATION);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move);
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case LIBERATION: {
                DetailedIntent detail = new DetailedIntent(this, PARALYSIS, DetailedIntent.PARALYSIS_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, STATUS, DetailedIntent.DAZED_TEXTURE, DetailedIntent.TargetType.DISCARD_PILE);
                detailsList.add(detail2);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "ButterflyAtk", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", null, enemy, this);
    }

}