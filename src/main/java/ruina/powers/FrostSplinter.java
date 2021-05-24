package ruina.powers;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.actions.FreezeCardInHandAction;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class FrostSplinter extends AbstractUnremovablePower {
    public static final String POWER_ID = makeID(FrostSplinter.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final int FREEZE_AMOUNT = 1;
    private static final int ENERGY_AMOUNT = 1;

    public FrostSplinter(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        amount2 = ENERGY_AMOUNT;
    }

    @Override
    public void atStartOfTurnPostDraw() {
        flash();
        atb(new GainEnergyAction(ENERGY_AMOUNT));
        atb(new DrawCardAction(owner, amount));
        atb(new FreezeCardInHandAction(owner, FREEZE_AMOUNT));
    }

    @Override
    public void updateDescription() {
        description = String.format(amount > 1 ? DESCRIPTIONS[1] : DESCRIPTIONS[0], amount, FREEZE_AMOUNT);
    }
}