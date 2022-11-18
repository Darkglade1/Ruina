package ruina.relics;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.powers.Bleed;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTargetTop;

public class BrokenHeart extends AbstractEasyRelic {
    public static final String ID = makeID(BrokenHeart.class.getSimpleName());

    private static final int BLEED = 3;
    private static final int ENERGY = 2;

    private boolean firstTurn;

    public BrokenHeart() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    public void atPreBattle() {
        this.firstTurn = true;
    }

    public void atTurnStart() {
        if (this.firstTurn) {
            this.flash();
            this.addToTop(new GainEnergyAction(ENERGY));
            applyToTargetTop(adp(), adp(), new Bleed(adp(), BLEED, false));
            this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.firstTurn = false;
        }

    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + BLEED + DESCRIPTIONS[1];
    }
}
