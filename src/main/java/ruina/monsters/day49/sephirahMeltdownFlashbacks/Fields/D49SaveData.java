package ruina.monsters.day49.sephirahMeltdownFlashbacks.Fields;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class D49SaveData {
    @SpirePatch(clz = AbstractPlayer.class, method = SpirePatch.CLASS)
    public static class Fields {
        public static SpireField<Boolean> defeatedMalkuth = new SpireField<>(() -> false);
        public static SpireField<Boolean> defeatedYesod = new SpireField<>(() -> false);
        public static SpireField<Boolean> defeatedNetzach = new SpireField<>(() -> false);
        public static SpireField<Boolean> defeatedHod = new SpireField<>(() -> false);
        public static SpireField<Boolean> defeatedTiphereth = new SpireField<>(() -> false);
        public static SpireField<Boolean> defeatedGebura = new SpireField<>(() -> false);
        public static SpireField<Boolean> defeatedChesed = new SpireField<>(() -> false);
        public static SpireField<Boolean> defeatedBinah = new SpireField<>(() -> false);
        public static SpireField<Boolean> defeatedHokma = new SpireField<>(() -> false);

        public static SpireField<Boolean> isBriahFloorsOpen = new SpireField<>(() -> false);
        public static SpireField<Boolean> isAtziluthFloorsOpen = new SpireField<>(() -> false);
        public static SpireField<Boolean> isKeterOpen = new SpireField<>(() -> false);

    }
}
