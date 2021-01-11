package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.BarricadePower;

import static ruina.util.Wiz.atb;

//because apparently the block of ALL enemies fall off at the end of the player's turn zzzzzzz
public class InvisibleBarricadePower extends AbstractEasyPower implements InvisiblePower {
    public static final String POWER_ID = BarricadePower.POWER_ID;

    public InvisibleBarricadePower(AbstractCreature owner) {
        super("", PowerType.BUFF, false, owner, -1);
        this.ID = POWER_ID;
    }

    @Override
    public void duringTurn() {
        atb(new RemoveAllBlockAction(owner, owner));
    }

    @Override
    public void updateDescription() {
        this.description = "";
    }
}
