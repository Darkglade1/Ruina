package ruina.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public abstract class AbstractUnremovablePower extends AbstractEasyPower {
    public boolean isUnremovable = true;

    public AbstractUnremovablePower(String NAME, String ID, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount) {
        super(NAME, ID, powerType, isTurnBased, owner, amount);
    }
}