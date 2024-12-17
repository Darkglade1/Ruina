package ruina.powers;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;

public class RuinaPlatedArmor extends PlatedArmorPower {

    public RuinaPlatedArmor(AbstractCreature owner, int amt) {
        super(owner, amt);
    }

    public void onRemove() {

    }

    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        if (!owner.isDeadOrEscaped()) {
            this.flash();
            this.addToBot(new GainBlockAction(this.owner, this.owner, this.amount));
        }
    }
}
