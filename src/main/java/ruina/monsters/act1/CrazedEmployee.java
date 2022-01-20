package ruina.monsters.act1;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class CrazedEmployee extends AbstractRuinaMonster
{
    public static final String ID = makeID(CrazedEmployee.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte TREMBLING_MOTION = 0;
    private static final byte SHAKING_BLOW = 1;

    private final int DEBUFF = calcAscensionSpecial(1);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int SELF_DEBUFF = 1;

    private final int debuff;

    public static final String POWER_ID = makeID("Song");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public CrazedEmployee() {
        this(0.0f, 0.0f, 0);
    }

    public CrazedEmployee(final float x, final float y, int debuff) {
        super(NAME, ID, 140, 0.0F, 0, 220.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("CrazedEmployee/Spriter/CrazedEmployee.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(24), calcAscensionTankiness(27));
        addMove(TREMBLING_MOTION, Intent.DEBUFF);
        addMove(SHAKING_BLOW, Intent.ATTACK, calcAscensionDamage(5));
        this.debuff = debuff;
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, STRENGTH) {
            @Override
            public void atEndOfRound() {
                flash();
                applyToTarget(owner, owner, new StrengthPower(owner, amount));
                applyToTarget(owner, owner, new VulnerablePower(owner, SELF_DEBUFF, false));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + SELF_DEBUFF + POWER_DESCRIPTIONS[2];
            }
        });
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        if (firstMove) {
            firstMove = false;
        }

        switch (this.nextMove) {
            case TREMBLING_MOTION: {
                blockAnimation();
                if (debuff == 0) {
                    applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                } else if (debuff == 1) {
                    applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                }
                resetIdle();
                break;
            }
            case SHAKING_BLOW: {
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
        if (debuff == 2) {
            setMoveShortcut(SHAKING_BLOW, MOVES[SHAKING_BLOW]);
        } else if (firstMove) {
            setMoveShortcut(TREMBLING_MOTION, MOVES[TREMBLING_MOTION]);
        } else {
            setMoveShortcut(SHAKING_BLOW, MOVES[SHAKING_BLOW]);
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntVert", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

}