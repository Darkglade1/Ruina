package ruina.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

import static ruina.util.Wiz.atb;

public class ParadiseLostPower extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID("ParadiseLostPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final int threshold;
    private int exhaustNum;

    public ParadiseLostPower(AbstractCreature owner, int damage, int amount, int threshold) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.amount = damage;
        this.exhaustNum = amount;
        this.amount2 = 0;
        this.threshold = threshold;
        updateDescription();
    }

    @Override
    public void atStartOfTurnPostDraw() {
        atb(new ExhaustAction(exhaustNum, false, true, true));
    }

    @Override
    public void onExhaust(AbstractCard card) {
        amount2++;
        if (amount2 % threshold == 0) {
            amount2 = 0;
            this.flash();
            atb(new DamageAllEnemiesAction(this.owner, DamageInfo.createDamageMatrix(amount, true), DamageInfo.DamageType.HP_LOSS, AbstractGameAction.AttackEffect.FIRE, true));
        } else {
            flashWithoutSound();
        }
        updateDescription();
    }

    @Override
    public void stackPower(int stackamount) {
        amount += stackamount;
        exhaustNum ++;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (exhaustNum == 1) {
            this.description = DESCRIPTIONS[0] + exhaustNum + DESCRIPTIONS[1] + DESCRIPTIONS[3] + threshold + DESCRIPTIONS[4] + amount + DESCRIPTIONS[5] + DESCRIPTIONS[6] + amount2 + DESCRIPTIONS[7];
        } else {
            this.description = DESCRIPTIONS[0] + exhaustNum + DESCRIPTIONS[2] + DESCRIPTIONS[3] + threshold + DESCRIPTIONS[4] + amount + DESCRIPTIONS[5] + DESCRIPTIONS[6] + amount2 + DESCRIPTIONS[7];
        }
    }
}
