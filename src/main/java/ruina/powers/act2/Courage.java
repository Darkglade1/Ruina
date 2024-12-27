package ruina.powers.act2;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.applyToTarget;

public class Courage extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Courage.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    AbstractCreature road;

    public Courage(AbstractCreature owner, int amount, AbstractCreature road) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.road = road;
        updateDescription();
    }

    @Override
    public void onSpecificTrigger() {
        flash();
        applyToTarget(owner, owner, new StrengthPower(owner, amount));
        applyToTarget(owner, owner, new LoseStrengthPower(owner, amount));
    }

    @Override
    public void updateDescription() {
        if (road != null) {
            description = POWER_DESCRIPTIONS[0] + FontHelper.colorString(road.name, "y") + POWER_DESCRIPTIONS[1] + amount + POWER_DESCRIPTIONS[2];
        }
    }
}
