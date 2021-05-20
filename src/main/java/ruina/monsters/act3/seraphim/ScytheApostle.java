package ruina.monsters.act3.seraphim;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.WingsOfGrace;

import java.util.ArrayList;

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

    private final int POWER_STRENGTH = calcAscensionSpecial(8);

    public static final String POWER_ID = makeID("Judas");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final Prophet prophet;

    public ScytheApostle(final float x, final float y, Prophet parent) {
        super(NAME, ID, 75, -5.0F, 0, 160.0f, 185.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ScytheApostle/Spriter/ScytheApostle.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(52), calcAscensionTankiness(56));
        addMove(FOLLOW_THEE, Intent.ATTACK, calcAscensionDamage(15));
        addMove(THY_WILL_BE_DONE, Intent.ATTACK, calcAscensionDamage(7), 2, true);
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
            case FOLLOW_THEE:
                slashDownAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
//        if (!this.lastTwoMoves(FOLLOW_THEE)) {
//            possibilities.add(FOLLOW_THEE);
//        }
//        if (!this.lastTwoMoves(THY_WILL_BE_DONE)) {
//
//        }
        possibilities.add(THY_WILL_BE_DONE);
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
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
        atb(new ApplyPowerAction(this, this, new WingsOfGrace(this, calcAscensionSpecial(2))));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, POWER_STRENGTH) {
            @Override
            public void onSpecificTrigger() {
                applyToTargetNextTurn(owner, new StrengthPower(owner, amount));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
        createIntent();
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