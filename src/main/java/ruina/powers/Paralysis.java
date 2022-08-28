package ruina.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

import static ruina.util.Wiz.atb;

public class Paralysis extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID("Paralysis");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justApplied = true;

    private static final float BASE_REDUCTION = 0.5f;
    private static final float STACK_REDUCTION = 0.25f;
    private static final int MAX_STACKS = 3;

    public Paralysis(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.DEBUFF, false, owner, amount);
        this.priority = 99;
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * (BASE_REDUCTION - (STACK_REDUCTION * (amount - 1)));
        } else {
            return damage;
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        if (this.amount == 0) {
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }

        if (this.amount > MAX_STACKS) {
            this.amount = MAX_STACKS;
        }
        justApplied = true;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            this.flash();
            atb(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }

    }

    @Override
    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        atb(new RemoveSpecificPowerAction(owner, owner, this));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + (int)((BASE_REDUCTION + (STACK_REDUCTION * (amount - 1))) * 100) + DESCRIPTIONS[1];
    }
}
