package ruina.powers.act2;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;

import static ruina.util.Wiz.block;

public class PointlessHate extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(PointlessHate.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public PointlessHate(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        this.flash();
        block(owner, amount);
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
    }
}
