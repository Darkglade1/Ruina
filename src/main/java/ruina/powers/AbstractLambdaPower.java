package ruina.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public abstract class AbstractLambdaPower extends AbstractEasyPower {
    public AbstractLambdaPower(String name, String id, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount) {
        super(name, id, powerType, isTurnBased, owner, amount);
    }

    public abstract void updateDescription();
}
