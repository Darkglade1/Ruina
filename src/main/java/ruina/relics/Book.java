package ruina.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

public class Book extends AbstractEasyRelic {
    public static final String ID = makeID(Book.class.getSimpleName());

    private static final int HEAL = 35;

    public Book() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onVictory() {
        this.flash();
        this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        if (adp().currentHealth > 0) {
            adp().heal((int) (adp().maxHealth * ((float)HEAL / 100)));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + HEAL + DESCRIPTIONS[1];
    }
}
