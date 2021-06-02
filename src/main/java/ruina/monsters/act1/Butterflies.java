package ruina.monsters.act1;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Paralysis;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Butterflies extends AbstractRuinaMonster
{
    public static final String ID = makeID(Butterflies.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte TRANQUILITY = 0;
    private static final byte LIBERATION = 1;

    private final int STATUS = calcAscensionSpecial(1);
    private final int PARALYSIS = calcAscensionSpecial(1);

    public Butterflies() {
        this(0.0f, 0.0f);
    }

    public Butterflies(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 100.0f, 160.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Butterflies/Spriter/Butterflies.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(11), calcAscensionTankiness(14));
        int damage = 4;
        if (AbstractDungeon.ascensionLevel >= 2) {
           damage = 5;
        }
        addMove(TRANQUILITY, Intent.ATTACK, damage);
        addMove(LIBERATION, Intent.DEBUFF);
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

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
        setMoveShortcut(move, MOVES[move]);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "ButterflyAtk", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", null, enemy, this);
    }

}