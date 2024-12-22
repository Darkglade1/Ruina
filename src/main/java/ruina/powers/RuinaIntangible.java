package ruina.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import ruina.monsters.act2.BadWolf;

public class RuinaIntangible extends IntangiblePlayerPower {
    private boolean justApplied;

    public RuinaIntangible(AbstractCreature owner, int amount, boolean justApplied) {
        super(owner, amount);
        this.justApplied = justApplied;
    }

    @Override
    public void onRemove() {
        if (owner instanceof BadWolf) {
            ((BadWolf) owner).changePhase(1);
        }
    }

    @Override
    public void atEndOfRound() {
        if (justApplied) {
            justApplied = false;
        } else {
            super.atEndOfRound();
        }
    }
}
