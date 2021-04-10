package ruina.powers;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.NoDrawPower;
import ruina.RuinaMod;

import static ruina.util.Wiz.*;

public class ContractOfLight extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(ContractOfLight.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final int ENERGY = 2;

    public ContractOfLight(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.DEBUFF, false, owner, ENERGY);
    }

    public void atStartOfTurnPostDraw() {
        atb(new GainEnergyAction(amount));
        applyToTarget(adp(), adp(), new NoDrawPower(adp()));
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
        for (int i = 0; i < amount; i++) {
            description += "[E] ";
        }
        description += DESCRIPTIONS[1];
    }
}