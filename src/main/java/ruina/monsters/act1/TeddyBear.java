package ruina.monsters.act1;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class TeddyBear extends AbstractRuinaMonster
{
    public static final String ID = makeID(TeddyBear.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte TIMID_ENDEARMENT = 0;
    private static final byte DISPLAY_AFFECTION = 1;

    private final int BLOCK = calcAscensionTankiness(11);
    private final int STRENGTH = calcAscensionSpecial(1);

    public static final String POWER_ID = makeID("Affection");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public TeddyBear() {
        this(0.0f, 0.0f);
    }

    public TeddyBear(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 250.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("TeddyBear/Spriter/TeddyBear.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(55), calcAscensionTankiness(59));
        addMove(TIMID_ENDEARMENT, Intent.DEFEND);
        addMove(DISPLAY_AFFECTION, Intent.ATTACK, calcAscensionDamage(12));
    }

    @Override
    public void usePreBattleAction() {
        playSound("TeddyOn", 2.0f);
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, STRENGTH) {
            @Override
            public int onAttacked(DamageInfo info, int damageAmount) {
                if (info.type == DamageInfo.DamageType.NORMAL && info.owner != owner) {
                    if (damageAmount < info.output) {
                        flash();
                        applyToTarget(owner, owner, new StrengthPower(owner, amount));
                    }
                }
                return damageAmount;
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
            case TIMID_ENDEARMENT: {
                blockAnimation();
                block(this, BLOCK);
                resetIdle();
                break;
            }
            case DISPLAY_AFFECTION: {
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
        if (lastMove(TIMID_ENDEARMENT)) {
            setMoveShortcut(DISPLAY_AFFECTION, MOVES[DISPLAY_AFFECTION]);
        } else {
            setMoveShortcut(TIMID_ENDEARMENT, MOVES[TIMID_ENDEARMENT]);
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "TeddyAtk", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", "TeddyBlock", this);
    }

}