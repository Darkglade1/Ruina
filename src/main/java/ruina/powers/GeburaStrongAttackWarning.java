package ruina.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

public class GeburaStrongAttackWarning extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(GeburaStrongAttackWarning.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public GeburaStrongAttackWarning(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void updateDescription() {
        if(amount == 0){ description = DESCRIPTIONS[0]; }
        else if(amount == 1){ description = DESCRIPTIONS[0] + String.format(DESCRIPTIONS[1], amount); }
        else { description = DESCRIPTIONS[0] + String.format(DESCRIPTIONS[2], amount); }
    }
}