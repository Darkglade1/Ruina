package ruina.monsters.act3.seraphim;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.WingsOfGrace;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class StaffApostle extends AbstractRuinaMonster {
    public static final String ID = makeID(StaffApostle.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte THY_WORDS = 0;
    private static final byte GIVE_US_REST = 1;

    private final int STRENGTH = calcAscensionSpecial(2);
    private final int BLOCK = calcAscensionTankiness(7);
    private final int BLOCK_STRENGTH = calcAscensionSpecial(1);

    private final Prophet prophet;

    public StaffApostle(final float x, final float y, Prophet parent) {
        super(NAME, ID, 50, -5.0F, 0, 160.0f, 185.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("StaffApostle/Spriter/StaffApostle.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(42), calcAscensionTankiness(48));
        addMove(THY_WORDS, Intent.ATTACK_BUFF, calcAscensionDamage(6));
        addMove(GIVE_US_REST, Intent.DEFEND_BUFF);
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
            case THY_WORDS:
                attackAnimation(adp());
                dmg(adp(), info);
                for (AbstractMonster mo : monsterList()) {
                    if (!(mo instanceof AbstractAllyMonster)) {
                        applyToTarget(mo, this, new StrengthPower(mo, STRENGTH));
                    }
                }
                resetIdle();
                break;
            case GIVE_US_REST:
                specialAnimation();
                for (AbstractMonster mo : monsterList()) {
                    if (!(mo instanceof AbstractAllyMonster)) {
                        block(mo, BLOCK);
                        applyToTarget(mo, this, new StrengthPower(mo, BLOCK_STRENGTH));
                    }
                }
                resetIdle();
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastTwoMoves(THY_WORDS)) {
            possibilities.add(THY_WORDS);
        }
        if (!this.lastTwoMoves(GIVE_US_REST)) {
            possibilities.add(GIVE_US_REST);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "ApostleWand", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", null, this);
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