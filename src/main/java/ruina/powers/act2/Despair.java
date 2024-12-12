package ruina.powers.act2;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;

public class Despair extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(Despair.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Despair(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
    }
}
