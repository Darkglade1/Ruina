package ruina.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

import static ruina.util.Wiz.atb;

public class EnergyLossNextTurn extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(EnergyLossNextTurn.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public EnergyLossNextTurn(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.DEBUFF, false, owner, amount);
        this.loadRegion("bias");
    }

    @Override
    public void atStartOfTurn() {
        this.flash();
        this.addToBot(new LoseEnergyAction(this.amount));
        atb(new RemoveSpecificPowerAction(owner, owner, this));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}
