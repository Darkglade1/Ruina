package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.BarricadePower;

import static ruina.util.Wiz.atb;

//because apparently the block of ALL enemies fall off at the end of the player's turn zzzzzzz
public class InvisibleBarricadePower extends AbstractEasyPower implements InvisiblePower {
    public static final String POWER_ID = BarricadePower.POWER_ID;
    public boolean justGainedBlock = false;

    public InvisibleBarricadePower(AbstractCreature owner) {
        super("", POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void duringTurn() {
        if (justGainedBlock) {
            justGainedBlock = false;
        } else {
            atb(new RemoveAllBlockAction(owner, owner));
        }
    }

    @Override
    public void updateDescription() {
        this.description = "";
    }
}
