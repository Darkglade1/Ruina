package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.DrawReductionPower;
import ruina.monsters.AbstractAllyMonster;
import ruina.util.TexLoader;

import static ruina.RuinaMod.getModID;
import static ruina.RuinaMod.makeRelicPath;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class InvisiblePermanentDrawReductionPower extends AbstractUnremovablePower implements InvisiblePower {
    public static final String POWER_ID = DrawReductionPower.POWER_ID;
    public InvisiblePermanentDrawReductionPower(AbstractCreature owner, int amount) {
        super("", POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    public void onInitialApplication() {
        --AbstractDungeon.player.gameHandSize;
    }


    @Override
    public void updateDescription() {
        this.description = "";
    }
}
