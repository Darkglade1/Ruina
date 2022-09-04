package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

public class CenterOfAttention extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(CenterOfAttention.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public CenterOfAttention(AbstractCreature owner) {
        super(NAME, POWER_ID, NeutralPowertypePatch.NEUTRAL, false, owner, 0);
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}