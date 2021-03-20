package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.BarricadePower;

import static ruina.util.Wiz.atb;

//because apparently the block of ALL enemies fall off at the end of the player's turn zzzzzzz
public class InvisibleAllyBarricadePower extends AbstractUnremovablePower implements InvisiblePower {
    public static final String POWER_ID = BarricadePower.POWER_ID;

    public InvisibleAllyBarricadePower(AbstractCreature owner) {
        super("", POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void atEndOfRound() {
        atb(new RemoveAllBlockAction(owner, owner));
    }

    @Override
    public void updateDescription() {
        this.description = "";
    }
}
