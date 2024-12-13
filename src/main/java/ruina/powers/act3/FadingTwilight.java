package ruina.powers.act3;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.act3.Twilight;
import ruina.powers.AbstractUnremovablePower;

public class FadingTwilight extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(FadingTwilight.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int EGG_CYCLE_TURN_NUM;
    private final int dmgThreshold;

    public FadingTwilight(AbstractCreature owner, int amount, int dmgThreshold) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.EGG_CYCLE_TURN_NUM = amount;
        this.dmgThreshold = dmgThreshold;
        updateDescription();
    }

    @Override
    public void atEndOfRound() {
        if (owner instanceof Twilight) {
            if (((Twilight) owner).getNumEggsLeft() > 1) {
                amount--;
                if (amount <= 0) {
                    flash();
                    ((Twilight) owner).cycleEgg();
                    amount = EGG_CYCLE_TURN_NUM;
                } else {
                    flashWithoutSound();
                }
            }
        }
    }

    @Override
    public void updateDescription() {
        if (amount2 <= 0) {
            amount2 = dmgThreshold;
        }
        description = POWER_DESCRIPTIONS[0] + EGG_CYCLE_TURN_NUM + POWER_DESCRIPTIONS[1] + amount2 + POWER_DESCRIPTIONS[2];
    }
}
