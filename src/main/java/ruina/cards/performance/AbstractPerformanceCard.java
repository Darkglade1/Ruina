package ruina.cards.performance;

import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.AbstractRuinaCard;

public abstract class AbstractPerformanceCard extends AbstractRuinaCard {

    public AbstractPerformanceCard(final String cardID, final int cost) {
        super(cardID, cost, CardType.STATUS, CardRarity.SPECIAL, CardTarget.NONE, CardColor.COLORLESS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            EndOfTurnEffect();
        }
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        if (!retain) {
            this.dontTriggerOnUseCard = true;
            AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
        }
    }

    @Override
    public void onRetained() {
        if (AbstractDungeon.ascensionLevel >= 19) {
            flash();
            EndOfTurnEffect();
        }
    }

    @Override
    public void triggerWhenDrawn() {
        if (AbstractDungeon.ascensionLevel >= 19) {
            this.retain = true;
        }
    }

    public void EndOfTurnEffect() {

    }
}