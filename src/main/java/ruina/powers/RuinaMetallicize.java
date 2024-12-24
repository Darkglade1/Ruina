package ruina.powers;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.MetallicizePower;

public class RuinaMetallicize extends MetallicizePower {

    public RuinaMetallicize(AbstractCreature owner, int amt) {
        super(owner, amt);
    }

    @Override
    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        if (!owner.isDeadOrEscaped()) {
            this.flash();
            this.addToBot(new GainBlockAction(this.owner, this.owner, this.amount));
        }
    }
}
