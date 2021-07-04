package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;

public class OurGalaxyAction extends AbstractGameAction {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private AbstractPlayer p;

    public OurGalaxyAction(int amount) {
        this.p = AbstractDungeon.player;
        this.setValues(this.p, AbstractDungeon.player, amount);
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_MED;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_MED) {
            AbstractCard card;
            CardGroup cardGroup = new CardGroup(CardGroupType.UNSPECIFIED);
            for (AbstractCard c : p.drawPile.group) {
                if (c.costForTurn >= 0 && c.costForTurn <= amount) {
                    cardGroup.addToRandomSpot(c);
                }
            }

            if (cardGroup.size() == 0) {
                this.isDone = true;
            } else if (cardGroup.size() == 1) {
                card = cardGroup.getTopCard();
                playCard(card);
                this.isDone = true;
            } else {
                AbstractDungeon.gridSelectScreen.open(cardGroup, 1, TEXT[0], false);
                this.tickDuration();
            }
        } else {
            if (AbstractDungeon.gridSelectScreen.selectedCards.size() != 0) {
                playCard(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
            this.tickDuration();
        }
    }

    private void playCard(AbstractCard card) {
        AbstractCard tmp = card.makeStatEquivalentCopy();
        AbstractDungeon.player.limbo.addToBottom(tmp);
        tmp.current_x = card.current_x;
        tmp.current_y = card.current_y;
        tmp.target_x = (float)Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
        tmp.target_y = (float)Settings.HEIGHT / 2.0F;
        tmp.purgeOnUse = true;
        AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng), card.energyOnUse, true, true), true);
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("OurGalaxyAction"));
        TEXT = uiStrings.TEXT;
    }
}
