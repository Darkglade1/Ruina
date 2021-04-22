package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.ShowCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import ruina.monsters.act2.Jack;

import static ruina.util.Wiz.adp;

public class JackStealAction extends AbstractGameAction {
    private Jack jack;
    private float startingDuration;
    private AbstractCard card = null;

    public JackStealAction(Jack jack) {
        this.jack = jack;
        this.duration = Settings.ACTION_DUR_FAST;
        this.startingDuration = Settings.ACTION_DUR_FAST;
        this.actionType = ActionType.WAIT;
    }

    public void update() {
        if (this.duration == this.startingDuration) {
            if (!adp().drawPile.isEmpty()) {
                this.card = adp().drawPile.getTopCard();
                adp().drawPile.removeCard(this.card);
                adp().limbo.addToBottom(this.card);
                jack.stolenCards.add(this.card);
                this.card.setAngle(0.0F);
                this.card.targetDrawScale = 0.75F;
                this.card.target_x = (float)Settings.WIDTH / 2.0F;
                this.card.target_y = (float)Settings.HEIGHT / 2.0F;
                this.card.lighten(false);
                this.card.unfadeOut();
                this.card.unhover();
                this.card.untip();
                this.card.stopGlowing();
            }
        }
        this.tickDuration();
        if (this.isDone && this.card != null) {
            addToTop(new ShowCardAction(this.card));
        }
    }
}
