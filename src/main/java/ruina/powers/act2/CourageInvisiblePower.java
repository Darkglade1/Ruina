package ruina.powers.act2;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.powers.AbstractUnremovablePower;

public class CourageInvisiblePower extends AbstractUnremovablePower implements InvisiblePower {

    public CourageInvisiblePower(AbstractCreature owner) {
        super("", "", PowerType.BUFF, false, owner, -1);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null && info.owner != this.owner) {
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                AbstractPower power = mo.getPower(Courage.POWER_ID);
                if (power != null) {
                    power.onSpecificTrigger();
                }
            }
        }
        return damageAmount;
    }
}
