package ruina.monsters;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.util.AdditionalIntent;

import java.util.ArrayList;

public abstract class AbstractDeckMonster extends AbstractCardMonster {

    private CardGroup masterDeck = new CardGroup(CardGroup.CardGroupType.HAND);
    private CardGroup draw = new CardGroup(CardGroup.CardGroupType.HAND);
    private CardGroup discard = new CardGroup(CardGroup.CardGroupType.HAND);
    private CardGroup purge = new CardGroup(CardGroup.CardGroupType.HAND);

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
        masterDeck.shuffle();
        draw.initializeDeck(masterDeck);
    }

    public AbstractCard topDeckCardForMoveAction(){
        if(draw.isEmpty() && discard.isEmpty()){
            System.out.println("oops");
            return new Madness();
        }
        else if(draw.isEmpty()){ shuffle(); }
        AbstractCard c = draw.getTopCard();
        if(c != null){
            if(c.exhaust){ moveToExhaust(c); }
            else { moveToDiscard(c); }
        }
        return c != null ? c : new Madness();
    }

    public void shuffle(){
        if(!discard.isEmpty()){
            for(AbstractCard c: discard.group){ draw.addToBottom(c); }
            draw.shuffle();
        }
    }

    public void moveToDiscard(AbstractCard c){
        draw.removeCard(c);
        discard.addToBottom(c);
    }

    public void moveToExhaust(AbstractCard c){
        draw.removeCard(c);
        purge.addToBottom(c);
    }


}
