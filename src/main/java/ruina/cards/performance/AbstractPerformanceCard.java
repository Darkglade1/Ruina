package ruina.cards.performance;

import ruina.cards.AbstractRuinaCard;

public abstract class AbstractPerformanceCard extends AbstractRuinaCard {

    public AbstractPerformanceCard(final String cardID, final int cost) {
        super(cardID, cost, CardType.STATUS, CardRarity.SPECIAL, CardTarget.NONE, CardColor.COLORLESS);
    }
}