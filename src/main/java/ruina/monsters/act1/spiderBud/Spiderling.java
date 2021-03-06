package ruina.monsters.act1.spiderBud;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Slimed;
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

public class Spiderling extends AbstractRuinaMonster
{
    public static final String ID = makeID(Spiderling.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte THIN_WEBBING = 0;
    private static final byte STINGY_FANGS = 1;

    private final int STATUS = 1;

    public Spiderling() {
        this(0.0f, 0.0f);
    }

    public Spiderling(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 200.0f, 165.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Spiderling/Spriter/Spiderling.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(10), calcAscensionTankiness(12));
        addMove(THIN_WEBBING, Intent.ATTACK_DEBUFF, calcAscensionSpecial(2));
        addMove(STINGY_FANGS, Intent.ATTACK, calcAscensionDamage(5));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

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
        setMoveShortcut(move, MOVES[move]);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "SpiderBabyAtk", enemy, this);
    }

}