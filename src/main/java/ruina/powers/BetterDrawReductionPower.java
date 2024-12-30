package ruina.powers;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.DrawReductionPower;

public class BetterDrawReductionPower extends DrawReductionPower {

    public BetterDrawReductionPower(AbstractCreature owner, int amt) {
        super(owner, amt);
    }

    @Override
    public void atEndOfRound() {
        this.addToBot(new ReducePowerAction(this.owner, this.owner, this, 1));
    }
}
