package ruina.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ScreenShake;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

public class FalsePresent extends AbstractEasyRelic {
    public static final String ID = makeID(FalsePresent.class.getSimpleName());

    private static final int NUM_DISCOUTNS = 2;
    private static final int MAX_HP_LOSS = 7;

    public FalsePresent() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void atPreBattle() {
        this.counter = 0;
    }

    @Override
    public void onEquip() {
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
        CardCrawlGame.sound.play("BLUNT_FAST");
        adp().decreaseMaxHealth(MAX_HP_LOSS);
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
        return DESCRIPTIONS[0] + MAX_HP_LOSS + DESCRIPTIONS[1] + NUM_DISCOUTNS + DESCRIPTIONS[2];
    }
}
