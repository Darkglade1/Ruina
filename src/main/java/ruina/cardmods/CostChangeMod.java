package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import ruina.RuinaMod;

public class CostChangeMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID("CostChangeMod");

    private final int costChange;

    public CostChangeMod(int amount) {
        this.costChange = amount;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new CostChangeMod(costChange);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.cost >= 0) {
            card.cost += costChange;
            card.costForTurn = card.cost;
        }
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
