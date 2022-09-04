package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import ruina.powers.PlayerBlackSilence;

import static ruina.util.Wiz.adp;

// A patch to prevent Roland from drinking potions
public class StopRolandDrinking {

    @SpirePatch(
            clz = AbstractPotion.class,
            method = "canUse"
    )
    public static class StopIt {
        @SpirePostfixPatch()
        public static boolean GetSomeHelp(boolean original) {
            if (adp().hasPower(PlayerBlackSilence.POWER_ID)) {
                return false;
            }
            return original;
        }
    }


}