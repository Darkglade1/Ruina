package ruina.monsters.act3.seraphim;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Bleed;
import ruina.powers.WingsOfGrace;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SpearApostle extends AbstractRuinaMonster {
    public static final String ID = makeID(SpearApostle.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte FOR_HE_IS_HOLY = 0;
    private static final byte THE_WILL_OF_THE_LORD_BE_DONE = 1;

    private final int debuff = calcAscensionSpecial(1);
    private final int BLEED = calcAscensionSpecial(2);

    private final Prophet prophet;

    public SpearApostle(final float x, final float y, Prophet parent) {
        super(NAME, ID, 50, -5.0F, 0, 160.0f, 185.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SpearApostle/Spriter/SpearApostle.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(46), calcAscensionTankiness(52));
        addMove(FOR_HE_IS_HOLY, Intent.ATTACK_DEBUFF, calcAscensionDamage(9));
        addMove(THE_WILL_OF_THE_LORD_BE_DONE, Intent.ATTACK_DEBUFF, calcAscensionDamage(4), 2, true);
        prophet = parent;
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;
        if (info.base > -1) {
            info.applyPowers(this, adp());
        }
        switch (nextMove) {
            case FOR_HE_IS_HOLY:
                spearAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new WeakPower(adp(), debuff, true));
                resetIdle();
                break;
            case THE_WILL_OF_THE_LORD_BE_DONE:
                for (int i = 0; i < multiplier; i++) {
                    spearAnimation(adp());
                    dmg(adp(), info);
                    resetIdle();
                }
                applyToTarget(adp(), this, new Bleed(adp(), BLEED));
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastTwoMoves(FOR_HE_IS_HOLY)) {
            possibilities.add(FOR_HE_IS_HOLY);
        }
        if (!this.lastTwoMoves(THE_WILL_OF_THE_LORD_BE_DONE)) {
            possibilities.add(THE_WILL_OF_THE_LORD_BE_DONE);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
    }

    private void spearAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "ApostleSpear", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Block", null, this);
    }


    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        prophet.incrementApostleKills();
        for (int i = 0; i < prophet.minions.length; i++) {
            if (prophet.minions[i] == this) {
                prophet.minions[i] = null;
                break;
            }
        }
    }

    @Override
    public void usePreBattleAction() {
        atb(new ApplyPowerAction(this, this, new WingsOfGrace(this, calcAscensionSpecial(1))));
        createIntent();
    }
}