package ruina.monsters.act1.redShoes;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.VampireDamageActionButItCanFizzle;
import ruina.monsters.AbstractRuinaMonster;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class RightShoe extends AbstractRuinaMonster
{
    public static final String ID = makeID(RightShoe.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte OBSESSION = 0;
    private static final byte BURSTING_DESIRE = 1;

    private final int STRENGTH = calcAscensionSpecial(3);

    public RightShoe() {
        this(0.0f, 0.0f);
    }

    public RightShoe(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 220.0f, 285.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("RightShoe/Spriter/RightShoe.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(26), calcAscensionTankiness(28));
        addMove(OBSESSION, Intent.BUFF);
        addMove(BURSTING_DESIRE, Intent.ATTACK_BUFF, calcAscensionDamage(6));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case OBSESSION: {
                specialAnimation();
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle();
                break;
            }
            case BURSTING_DESIRE: {
                attackAnimation(adp());
                atb(new VampireDamageActionButItCanFizzle(adp(), info, AbstractGameAction.AttackEffect.NONE));
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(OBSESSION)) {
            possibilities.add(OBSESSION);
        }
        if (!this.lastTwoMoves(BURSTING_DESIRE)) {
            possibilities.add(BURSTING_DESIRE);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "ShoesAtk", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "ShoesOn", this);
    }

}