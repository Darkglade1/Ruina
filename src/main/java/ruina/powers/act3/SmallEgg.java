package ruina.powers.act3;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

public class SmallEgg extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(SmallEgg.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final int SMALL_EGG_COST_INCREASE;

    public SmallEgg(AbstractCreature owner, int amount, int costIncrease) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.SMALL_EGG_COST_INCREASE = costIncrease;
        updateDescription();
    }

    int counter = 0;
    @Override
    public void onCardDraw(AbstractCard card) {
        if (counter < amount) {
            card.setCostForTurn(card.costForTurn + SMALL_EGG_COST_INCREASE);
            card.flash();
            counter++;
        }
    }

    @Override
    public void atEndOfRound() {
        counter = 0;
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + SMALL_EGG_COST_INCREASE + POWER_DESCRIPTIONS[2];
    }
}
