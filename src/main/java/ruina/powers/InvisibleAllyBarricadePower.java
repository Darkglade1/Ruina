package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.BarricadePower;
import ruina.monsters.AbstractAllyMonster;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

//because apparently the block of ALL enemies fall off at the end of the player's turn zzzzzzz
public class InvisibleAllyBarricadePower extends AbstractUnremovablePower implements InvisiblePower, OnReceivePowerPower {
    public static final String POWER_ID = BarricadePower.POWER_ID;

    public InvisibleAllyBarricadePower(AbstractCreature owner) {
        super("", POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void atEndOfRound() {
        atb(new RemoveAllBlockAction(owner, owner));
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        //failsafe to stop the player from applying powers to allies
        if (owner instanceof AbstractAllyMonster) {
            AbstractAllyMonster ally = (AbstractAllyMonster) owner;
            if (ally.isAlly && !ally.isTargetableByPlayer && (source == adp() || source == null)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void updateDescription() {
        this.description = "";
    }
}
