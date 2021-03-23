package ruina.monsters;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.colorless.Madness;

import java.util.ArrayList;

public abstract class AbstractDeckMonster extends AbstractCardMonster {

    protected CardGroup masterDeck = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    protected CardGroup hand = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    protected CardGroup draw = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    protected CardGroup discard = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    protected CardGroup purge = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

    public AbstractDeckMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
    }

    public AbstractDeckMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
    }

    public AbstractDeckMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
    }

    public void initializeDeck(){
        createDeck();
        masterDeck.shuffle();
        for(AbstractCard c: masterDeck.group){
            draw.addToBottom(c);
        }
    }

    protected abstract void createDeck();

    public AbstractCard topDeckCardForMoveAction() {
        if (draw.isEmpty() && discard.isEmpty()) {
            System.out.println("oops");
            return new Madness();
        } else if (draw.isEmpty()) {
            shuffle();
        }
        AbstractCard c = draw.getTopCard();
        if (c != null) {
            moveToHand(c);
        }
//        System.out.println("Hand: " + hand);
//        System.out.println("Draw: " + draw);
//        System.out.println("Discard: " + discard);
        return c != null ? c : new Madness();
    }

    public void discardHand() {
        ArrayList<AbstractCard> cardsToExhaust = new ArrayList<>();
        ArrayList<AbstractCard> cardsToDiscard = new ArrayList<>();
        for (AbstractCard c : hand.group) {
            if (c != null) {
                if (c.exhaust) {
                    cardsToExhaust.add(c);
                } else {
                    cardsToDiscard.add(c);
                }
            }
        }
        for (AbstractCard card : cardsToExhaust) {
            moveToExhaust(card);
        }
        for (AbstractCard card : cardsToDiscard) {
            moveToDiscard(card);
        }
    }

    public void shuffle() {
        if (!discard.isEmpty()) {
            for (AbstractCard c : discard.group) {
                draw.addToBottom(c);
            }
            discard.clear();
            draw.shuffle();
        }
    }

    public void moveToDiscard(AbstractCard c){
        hand.removeCard(c);
        discard.addToBottom(c);
    }

    public void moveToExhaust(AbstractCard c){
        hand.removeCard(c);
        purge.addToBottom(c);
    }

    public void moveToHand(AbstractCard c) {
        draw.removeCard(c);
        hand.addToBottom(c);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        discardHand();
    }


}
