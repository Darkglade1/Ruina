package ruina.powers.act4;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.uninvitedGuests.normal.pluto.monster.Hokma;
import ruina.powers.AbstractUnremovablePower;

public class PriceOfTime extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(PriceOfTime.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public PriceOfTime(AbstractCreature owner, int amount, int cardsPerTurn) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.amount2 = cardsPerTurn;
        updateDescription();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        owner.addPower(new PriceOfTime(owner, 1, amount2));
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        if (this.amount >= amount2 && owner instanceof Hokma) {
            this.amount = 0;
            ((Hokma) owner).takeTurn();
        } else {
            flashWithoutSound();
        }
    }


    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount2 + POWER_DESCRIPTIONS[1];
    }
}
