package ruina.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

public class Unkillable extends AbstractEasyPower{
    public static final String POWER_ID = RuinaMod.makeID(Unkillable.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Unkillable(AbstractCreature owner) { super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1); }

    @Override
    public void updateDescription() { this.description = DESCRIPTIONS[0]; }
}