package ruina.monsters.act3.seraphim;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.WingsOfGrace;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class ScytheApostle extends AbstractRuinaMonster {
    public static final String ID = makeID(ScytheApostle.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte FOLLOW_THEE = 0;
    private static final byte THY_WILL_BE_DONE = 1;
    private static final byte PRESERVE_THEE = 2;
    private static final byte TEACH_US = 3;
    private final int followTheeBlock = calcAscensionTankiness(8);
    private final int teachUsWings = calcAscensionSpecial(1);
    private final int preserveTheeBlock = calcAscensionTankiness(7);
    private final int startingState;
    private final Prophet prophet;

    public ScytheApostle(final float x, final float y, Prophet parent, int startingState) {
        super(NAME, ID, 75, -5.0F, 0, 160.0f, 185.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ScytheApostle/Spriter/ScytheApostle.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(50), calcAscensionTankiness(56));
        addMove(FOLLOW_THEE, Intent.ATTACK_DEFEND, 9);
        addMove(THY_WILL_BE_DONE, Intent.ATTACK, 6, 2, true);
        addMove(PRESERVE_THEE, Intent.DEFEND);
        addMove(TEACH_US, Intent.BUFF);
        this.startingState = startingState;
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
            case TEACH_US:
                specialAnimation();
                applyToTarget(this, this, new WingsOfGrace(this, teachUsWings));
                resetIdle();
                break;
            case THY_WILL_BE_DONE:
                for (int i = 0; i < multiplier; i++) {
                    if (i == 0) {
                        slashUpAnimation(adp());
                    } else {
                        slashDownAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            case PRESERVE_THEE:
                specialAnimation();
                for (AbstractMonster m : monsterList()) {
                    if (!(m instanceof AbstractAllyMonster)) {
                        block(m, preserveTheeBlock);
                    }
                }
                resetIdle();
                break;
            case FOLLOW_THEE:
                slashDownAnimation(adp());
                dmg(adp(), info);
                block(this, followTheeBlock);
                resetIdle();
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        switch (startingState) {
            case 0:
                if (lastMove(TEACH_US)) {
                    setMoveShortcut(num <= 45 ? FOLLOW_THEE : THY_WILL_BE_DONE, num <= 45 ? MOVES[FOLLOW_THEE] : MOVES[THY_WILL_BE_DONE]);
                } else {
                    setMoveShortcut(TEACH_US, MOVES[TEACH_US]);
                }
                break;
            case 1:
                if (lastMove(FOLLOW_THEE)) {
                    setMoveShortcut(PRESERVE_THEE, MOVES[PRESERVE_THEE]);
                } else {
                    setMoveShortcut(FOLLOW_THEE, MOVES[FOLLOW_THEE]);
                }
                break;
            case 2:
                if (lastMove(THY_WILL_BE_DONE)) {
                    setMoveShortcut(num <= 45 ? TEACH_US : THY_WILL_BE_DONE, num <= 45 ? MOVES[TEACH_US] : MOVES[THY_WILL_BE_DONE]);
                } else {
                    setMoveShortcut(THY_WILL_BE_DONE, MOVES[THY_WILL_BE_DONE]);
                }
                break;
            case 3:
                if (lastMove(TEACH_US)) {
                    setMoveShortcut(THY_WILL_BE_DONE, MOVES[THY_WILL_BE_DONE]);
                } else if (lastMove(PRESERVE_THEE)) {
                    setMoveShortcut(TEACH_US, MOVES[TEACH_US]);
                } else {
                    setMoveShortcut(PRESERVE_THEE, MOVES[PRESERVE_THEE]);
                }
                break;
        }
    }

    private void slashUpAnimation(AbstractCreature enemy) {
        animationAction("SlashUp", "ApostleScytheUp", enemy, this);
    }

    private void slashDownAnimation(AbstractCreature enemy) {
        animationAction("SlashDown", "ApostleScytheDown", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Block", null, this);
    }


    @Override
    public void usePreBattleAction() {
        atb(new ApplyPowerAction(this, this, new WingsOfGrace(this, 1)));
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

}