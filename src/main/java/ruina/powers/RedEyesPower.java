package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.RuinaMod;

import static ruina.util.Wiz.atb;

public class RedEyesPower extends AbstractEasyPower implements OnReceivePowerPower {

    public static final String POWER_ID = RuinaMod.makeID("RedEyesPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public RedEyesPower(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.DEBUFF, false, owner, -1);
        this.priority = 99;
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (power.type == PowerType.DEBUFF && power.amount > 0) {
            power.amount *= 2;
            atb(new RemoveSpecificPowerAction(owner, owner, this));
        }
        return true;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}
