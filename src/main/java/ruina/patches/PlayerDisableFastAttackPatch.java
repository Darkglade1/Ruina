package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import ruina.powers.act5.PlayerBlackSilence;

public class PlayerDisableFastAttackPatch {
    @SpirePatch(
            clz= AbstractCreature.class,
            method="useFastAttackAnimation"
    )
    public static class RenderPatch {
        public static SpireReturn Prefix(AbstractCreature __instance) {
            if (__instance instanceof AbstractPlayer && __instance.hasPower(PlayerBlackSilence.POWER_ID)) { return SpireReturn.Return(null); }
            return SpireReturn.Continue();
        }
    }
}