package ruina.powers.act2;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;

import static ruina.util.Wiz.atb;

public class InvisibleEnergyPower extends AbstractEasyPower implements InvisiblePower {
    public static final String POWER_ID = RuinaMod.makeID(InvisibleEnergyPower.class.getSimpleName());

    public InvisibleEnergyPower(AbstractCreature owner, int amount) {
        super("", POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    public void onEnergyRecharge() {
        atb(new GainEnergyAction(amount));
    }

    @Override
    public void updateDescription() {
        this.description = "";
    }
}
