package ruina.monsters.act3.seraphim;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.WingsOfGrace;

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
    private static final byte DEVOTE_UNTO_THEE = 2;
    private static final byte THY_LIGHT_LEAD_ME = 3;

    private int forHeIsHolyDamage = calcAscensionDamage(7);
    private int forHeIsHolyWeak = calcAscensionSpecial(1);
    private int willOfTheLordDamage = calcAscensionDamage(5);
    private int devoteUntoTheeDamage = calcAscensionDamage(7);
    private int devoteUntoTheeBlock = calcAscensionTankiness(7);
    private int thyLightLeadMeDamage = calcAscensionDamage(4);

    private int startingState;
    private Prophet prophet;

    public SpearApostle(final float x, final float y, Prophet parent, int startingState) {
        super(NAME, ID, 50, -5.0F, 0, 130.0f, 165.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SpearApostle/Spriter/SpearApostle.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(FOR_HE_IS_HOLY, Intent.ATTACK_DEBUFF, forHeIsHolyDamage);
        addMove(THE_WILL_OF_THE_LORD_BE_DONE, Intent.ATTACK, willOfTheLordDamage, 3, true);
        addMove(DEVOTE_UNTO_THEE, Intent.ATTACK_DEFEND, devoteUntoTheeDamage);
        addMove(THY_LIGHT_LEAD_ME, Intent.ATTACK, thyLightLeadMeDamage, 2, true);
        this.startingState = startingState;
        prophet = parent;
        firstMove = true;
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
                dmg(adp(), info);
                atb(new ApplyPowerAction(adp(), this, new WeakPower(adp(), forHeIsHolyWeak, true)));
                break;
            case THE_WILL_OF_THE_LORD_BE_DONE:
            case THY_LIGHT_LEAD_ME:
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info);
                }
                break;
            case DEVOTE_UNTO_THEE:
                dmg(adp(), info);
                atb(new GainBlockAction(this, devoteUntoTheeBlock));
                break;
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                firstMove = false;
                isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        switch (startingState) {
            case 0:
                if (lastMove(THY_LIGHT_LEAD_ME)) {
                    setMoveShortcut(num <= 45 ? DEVOTE_UNTO_THEE : THE_WILL_OF_THE_LORD_BE_DONE, num <= 45 ? MOVES[DEVOTE_UNTO_THEE] : MOVES[THE_WILL_OF_THE_LORD_BE_DONE]);
                } else {
                    setMoveShortcut(THY_LIGHT_LEAD_ME, MOVES[THY_LIGHT_LEAD_ME]);
                }
                break;
            case 1:
                if (lastMove(FOR_HE_IS_HOLY)) {
                    setMoveShortcut(num <= 30 ? DEVOTE_UNTO_THEE : THY_LIGHT_LEAD_ME, num <= 30 ? MOVES[DEVOTE_UNTO_THEE] : MOVES[THY_LIGHT_LEAD_ME]);
                } else {
                    setMoveShortcut(FOR_HE_IS_HOLY, MOVES[FOR_HE_IS_HOLY]);
                }
                break;
            case 2:
                if (lastMove(THE_WILL_OF_THE_LORD_BE_DONE)) {
                    setMoveShortcut(num <= 45 ? DEVOTE_UNTO_THEE : THY_LIGHT_LEAD_ME, num <= 45 ? MOVES[DEVOTE_UNTO_THEE] : MOVES[THY_LIGHT_LEAD_ME]);
                } else {
                    setMoveShortcut(THE_WILL_OF_THE_LORD_BE_DONE, MOVES[THE_WILL_OF_THE_LORD_BE_DONE]);
                }
                break;
            case 3:
                if (lastMove(FOR_HE_IS_HOLY)) {
                    setMoveShortcut(THE_WILL_OF_THE_LORD_BE_DONE, MOVES[THE_WILL_OF_THE_LORD_BE_DONE]);
                } else if (lastMove(DEVOTE_UNTO_THEE)) {
                    setMoveShortcut(FOR_HE_IS_HOLY, MOVES[FOR_HE_IS_HOLY]);
                } else {
                    setMoveShortcut(DEVOTE_UNTO_THEE, MOVES[DEVOTE_UNTO_THEE]);
                }
                break;
        }
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
    }
}