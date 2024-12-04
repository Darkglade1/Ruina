package ruina.monsters.act1;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class AllAroundHelper extends AbstractRuinaMonster
{
    public static final String ID = makeID(AllAroundHelper.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte CHARGE = 0;
    private static final byte CLEAN = 1;

    private int DAMAGE_THRESHOLD = 9;
    private final int STRENGTH = calcAscensionSpecial(2);
    private final boolean attackFirst;

    public static final String POWER_ID = makeID("Pattern");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public AllAroundHelper() {
        this(0.0f, 0.0f, false);
    }

    public AllAroundHelper(final float x, final float y, boolean attackFirst) {
        super(NAME, ID, 140, 0.0F, 0, 250.0f, 215.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Helper/Spriter/Helper.scml"));
        this.type = EnemyType.ELITE;
        setHp(calcAscensionTankiness(50), calcAscensionTankiness(54));
        addMove(CHARGE, Intent.BUFF);
        addMove(CLEAN, Intent.ATTACK, calcAscensionDamage(7), 2);
        this.attackFirst = attackFirst;

        if (AbstractDungeon.ascensionLevel >= 3) {
            DAMAGE_THRESHOLD += 1;
        }
        if (AbstractDungeon.ascensionLevel >= 18) {
            DAMAGE_THRESHOLD += 1;
        }
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        if (attackFirst) {
            CustomDungeon.playTempMusicInstantly("Warning1");
            playSound("HelperOn", 8.0f);
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, DAMAGE_THRESHOLD) {
            @Override
            public void onInitialApplication() {
                amount2 = 0;
            }

            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (info.type == DamageInfo.DamageType.NORMAL && info.owner == owner && damageAmount > 0) {
                    amount2 += damageAmount;
                    if (amount2 >= amount) {
                        flash();
                        amount2 = 0;
                        atb(new RemoveDebuffsAction(owner));
                    }
                }
            }

            @Override
            public void atEndOfRound() {
                amount2 = 0;
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case CHARGE: {
                specialAnimation(adp());
                applyToTargetNextTurn(this, new StrengthPower(this, STRENGTH));
                applyToTargetNextTurn(this, new LoseStrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
            case CLEAN: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(adp());
                    dmg(adp(), info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (attackFirst && firstMove) {
            setMoveShortcut(CLEAN, MOVES[CLEAN]);
        } else if (lastMove(CHARGE)) {
            setMoveShortcut(CLEAN, MOVES[CLEAN]);
        } else {
            setMoveShortcut(CHARGE, MOVES[CHARGE]);
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "SwordVert", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", "HelperCharge", enemy, this);
    }

}