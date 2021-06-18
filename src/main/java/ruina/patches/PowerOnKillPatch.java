package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.monsters.act1.spiderBud.SpiderBud;
import ruina.monsters.act3.seraphim.ScytheApostle;
import ruina.powers.AClaw;

import static ruina.util.Wiz.monsterList;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "die",
        paramtypez = boolean.class
)

// A patch that allows a power to trigger upon a monster's death
public class PowerOnKillPatch {
    @SpirePostfixPatch
    public static void triggerOnKillPowers(AbstractMonster instance, boolean triggerRelics) {
        for (AbstractMonster mo : monsterList()) {
            for (AbstractPower power : mo.powers) {
                if (power instanceof AClaw) {
                    power.onSpecificTrigger();
                }
                if (power.ID.equals(ScytheApostle.POWER_ID)) {
                    power.onSpecificTrigger();
                }
                if (power.ID.equals(SpiderBud.POWER_ID)) {
                    power.onSpecificTrigger();
                }
            }
        }
    }
}
