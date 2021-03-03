package ruina.monsters.act3;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class RunawayBird extends AbstractRuinaMonster
{
    public static final String ID = makeID(RunawayBird.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte SWEEP = 0;
    private static final byte SHRIEK = 1;

    private final int STATUS = calcAscensionSpecial(1);

    public RunawayBird() {
        this(0.0f, 0.0f);
    }

    public RunawayBird(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0.0f, 200.0f, 220.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("RunawayBird/Spriter/RunawayBird.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(36), calcAscensionTankiness(42));
        addMove(SWEEP, Intent.ATTACK, calcAscensionDamage(10));
        addMove(SHRIEK, Intent.ATTACK_DEBUFF, calcAscensionDamage(6));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case SWEEP: {
                sweepAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case SHRIEK: {
                shriekAnimation(adp());
                dmg(adp(), info);
                intoDrawMo(new Dazed(), STATUS, this);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastTwoMoves(SWEEP)) {
            possibilities.add(SWEEP);
        }
        if (!this.lastTwoMoves(SHRIEK)) {
            possibilities.add(SHRIEK);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
    }

    private void sweepAnimation(AbstractCreature enemy) {
        animationAction("Sweep", "BirdSweep", enemy, this);
    }

    private void shriekAnimation(AbstractCreature enemy) {
        animationAction("Shriek", "BirdShout", enemy, this);
    }

}