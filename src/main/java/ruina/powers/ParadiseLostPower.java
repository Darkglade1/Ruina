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

    private final int damage;
    private final int threshold;
    private int exhaustNum;

    public ParadiseLostPower(AbstractCreature owner, int damage, int amount, int threshold) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.damage = damage;
        this.exhaustNum = amount;
        this.amount = 0;
        this.threshold = threshold;
        updateDescription();
    }

    @Override
    public void atStartOfTurnPostDraw() {
        atb(new ExhaustAction(exhaustNum, false));
    }

    @Override
    public void onExhaust(AbstractCard card) {
        amount++;
        if (amount % threshold == 0) {
            amount = 0;
            this.flash();
            atb(new DamageAllEnemiesAction(this.owner, DamageInfo.createDamageMatrix(damage, true), DamageInfo.DamageType.HP_LOSS, AbstractGameAction.AttackEffect.FIRE, true));
        } else {
            flashWithoutSound();
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        exhaustNum += stackAmount;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (exhaustNum == 1) {
            this.description = DESCRIPTIONS[0] + exhaustNum + DESCRIPTIONS[1] + DESCRIPTIONS[3] + threshold + DESCRIPTIONS[4] + damage + DESCRIPTIONS[5];
        } else {
            this.description = DESCRIPTIONS[0] + exhaustNum + DESCRIPTIONS[2] + DESCRIPTIONS[3] + threshold + DESCRIPTIONS[4] + damage + DESCRIPTIONS[5];
        }
    }
}
