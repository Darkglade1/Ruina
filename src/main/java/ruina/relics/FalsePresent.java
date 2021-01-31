package ruina.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

public class FalsePresent extends AbstractEasyRelic {
    public static final String ID = makeID(FalsePresent.class.getSimpleName());

    private static final int NUM_DISCOUTNS = 3;
    private static final int DRAW_REDUCTION = 1;
    private boolean firstTurn = true;

    public FalsePresent() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void atPreBattle() {
        firstTurn = true;
        adp().gameHandSize -= DRAW_REDUCTION;
        this.counter = 0;
    }

    @Override
    public void atTurnStartPostDraw() {
        if (firstTurn) {
            adp().gameHandSize += DRAW_REDUCTION;
            firstTurn = false;
        }
    }

    @Override
    public void onCardDraw(AbstractCard drawnCard) {
        if (counter < NUM_DISCOUTNS) {
            counter++;
            drawnCard.freeToPlayOnce = true;
            flash();
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + NUM_DISCOUTNS + DESCRIPTIONS[1] + DRAW_REDUCTION + DESCRIPTIONS[2];
    }
}
