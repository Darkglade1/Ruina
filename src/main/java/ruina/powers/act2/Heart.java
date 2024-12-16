package ruina.powers.act2;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.*;

public class Heart extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Heart.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final int energyGain;

    public Heart(AbstractCreature owner, int amount, int energyGain) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.energyGain = energyGain;
        updateDescription();
    }

    @Override
    public void atEndOfRound() {
        int str = EnergyPanel.totalCount * amount;
        if (str > 0) {
            applyToTarget(owner, owner, new StrengthPower(owner, str));
        }
    }

    @Override
    public void onRemove() {
        atb(new RemoveSpecificPowerAction(adp(), adp(), InvisibleEnergyPower.POWER_ID));
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0];
        for (int i = 0; i < energyGain; i++) {
            description += " [E]";
        }
        description += POWER_DESCRIPTIONS[1] + amount + POWER_DESCRIPTIONS[2];
    }
}
