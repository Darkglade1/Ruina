package ruina.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class LowellsGift extends AbstractEasyRelic {
    public static final String ID = makeID(LowellsGift.class.getSimpleName());

    private static final int STRENGTH = 2;
    private static final int DEXTERITY = 1;
    private static final int TURN_THRESHOLD = 3;

    public LowellsGift() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        this.counter = 0;
    }

    @Override
    public void atTurnStart() {
        if (!this.grayscale) {
            this.counter++;
        }

        if (this.counter == TURN_THRESHOLD) {
            this.flash();
            atb(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            applyToTarget(adp(), adp(), new StrengthPower(adp(), STRENGTH));
            applyToTarget(adp(), adp(), new DexterityPower(adp(), DEXTERITY));
            this.counter = -1;
            this.grayscale = true;
        }
    }

    @Override
    public void onVictory() {
        this.counter = -1;
        this.grayscale = false;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + STRENGTH + DESCRIPTIONS[1] + DEXTERITY + DESCRIPTIONS[2];
    }
}
