package ruina.powers.multiplayer;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.powers.AbstractUnremovablePower;

public class CourageInvisiblePowerMultiplayer extends AbstractUnremovablePower implements InvisiblePower {

    public CourageInvisiblePowerMultiplayer(AbstractCreature owner) {
        super("", "", PowerType.BUFF, false, owner, -1);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null && info.owner != this.owner) {
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (mo.hasPower(CourageMultiplayer.POWER_ID)) {
                    mo.getPower(CourageMultiplayer.POWER_ID).onSpecificTrigger();
                    break;
                }
            }
        }
        return damageAmount;
    }
}
