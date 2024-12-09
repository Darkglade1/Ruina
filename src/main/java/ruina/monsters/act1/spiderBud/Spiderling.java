package ruina.monsters.act1.spiderBud;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Spiderling extends AbstractRuinaMonster
{
    public static final String ID = makeID(Spiderling.class.getSimpleName());

    private static final byte THIN_WEBBING = 0;
    private static final byte STINGY_FANGS = 1;

    private final int STATUS = 1;

    public Spiderling() {
        this(0.0f, 0.0f);
    }

    public Spiderling(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 200.0f, 165.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Spiderling/Spriter/Spiderling.scml"));
        setHp(calcAscensionTankiness(10), calcAscensionTankiness(12));
        addMove(THIN_WEBBING, Intent.ATTACK_DEBUFF, calcAscensionSpecial(2));
        addMove(STINGY_FANGS, Intent.ATTACK, calcAscensionDamage(5));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case THIN_WEBBING: {
                attackAnimation(adp());
                dmg(adp(), info);
                intoDiscardMo(new Slimed(), STATUS, this);
                resetIdle();
                break;
            }
            case STINGY_FANGS: {
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
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastTwoMoves(THIN_WEBBING)) {
            possibilities.add(THIN_WEBBING);
        }
        if (!this.lastTwoMoves(STINGY_FANGS)) {
            possibilities.add(STINGY_FANGS);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move);
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case THIN_WEBBING: {
                DetailedIntent detail = new DetailedIntent(this, STATUS, DetailedIntent.SLIMED_TEXTURE, DetailedIntent.TargetType.DISCARD_PILE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "SpiderBabyAtk", 0.6f, enemy, this);
    }

}