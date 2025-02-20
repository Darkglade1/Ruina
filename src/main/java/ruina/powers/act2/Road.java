package ruina.powers.act2;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

public class Road extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Road.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Road(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void updateDescription() {
        if (amount == 0) {
            description = POWER_DESCRIPTIONS[3];
        } else if (amount == 1) {
            description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[2];
        } else {
            description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
        }
    }
}
