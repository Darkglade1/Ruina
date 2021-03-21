package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.FrailPower;

import static ruina.util.Wiz.applyToTargetTop;
import static ruina.util.Wiz.att;

public class InvisibleVibrationRemover extends AbstractUnremovablePower implements InvisiblePower {
    public static final String POWER_ID = BarricadePower.POWER_ID;

    public InvisibleVibrationRemover(AbstractCreature owner) {
        super("", POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void updateDescription() {
        this.description = "";
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type != DamageInfo.DamageType.THORNS && info.type != DamageInfo.DamageType.HP_LOSS && info.owner != null && info.owner != this.owner && damageAmount > 0) {
            if(info.owner.hasPower(Vibration.POWER_ID)){
                if(info.owner.getPower(Vibration.POWER_ID).amount > 1){ att(new ReducePowerAction(info.owner, info.owner, Vibration.POWER_ID, 1)); }
                else { att(new RemoveSpecificPowerAction(info.owner, info.owner, Vibration.POWER_ID)); }
            }
        }
        return damageAmount;
    }
}
