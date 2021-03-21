package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.BarricadePower;

import static ruina.util.Wiz.att;

public class InvisibleVibrationSender extends AbstractUnremovablePower implements InvisiblePower {
    public static final String POWER_ID = BarricadePower.POWER_ID;

    public InvisibleVibrationSender(AbstractCreature owner) {
        super("", POWER_ID, PowerType.BUFF, false, owner, -1);
    }
    @Override
    public void updateDescription() {
        this.description = "";
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type != DamageInfo.DamageType.THORNS && info.type != DamageInfo.DamageType.HP_LOSS && info.owner != null && info.owner != this.owner && damageAmount > 0) { att(new ApplyPowerAction(owner, owner, new Vibration(owner, 1))); }
        return damageAmount;
    }
}