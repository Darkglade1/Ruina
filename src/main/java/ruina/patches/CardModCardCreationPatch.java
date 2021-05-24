package ruina.patches;

import basemod.ReflectionHacks;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import ruina.cardmods.ContractsMod;
import ruina.relics.YesterdayPromiseRelic;

import static ruina.util.Wiz.adp;

public class CardModCardCreationPatch {
    @SpirePatch(clz = ShowCardAndAddToDiscardEffect.class, method = "update")
    @SpirePatch(clz = ShowCardAndAddToHandEffect.class, method = "update")
    @SpirePatch(clz = ShowCardAndAddToDrawPileEffect.class, method = "update")
    public static class InDraw {
        public static void Prefix(AbstractGameEffect __instance) {
            if (__instance.duration == (float) ReflectionHacks.getPrivateStatic(__instance.getClass(), "EFFECT_DUR")) {
                if (adp().hasRelic(YesterdayPromiseRelic.ID)) {
                    CardModifierManager.addModifier(ReflectionHacks.getPrivate(__instance, __instance.getClass(), "card"), new ContractsMod());
                }
            }
        }
    }
}