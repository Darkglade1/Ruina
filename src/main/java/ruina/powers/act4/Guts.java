package ruina.powers.act4;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

public class Guts extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Guts.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Guts(AbstractCreature owner, int GUTS_STRENGTH, int GUTS_METALLICIZE_GAIN) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, GUTS_STRENGTH);
        this.amount2 = GUTS_METALLICIZE_GAIN;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + amount2 + POWER_DESCRIPTIONS[2];
    }
}
