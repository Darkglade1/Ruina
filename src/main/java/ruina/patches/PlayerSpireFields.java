package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

@SpirePatch(
        clz=AbstractPlayer.class,
        method=SpirePatch.CLASS
)
public class PlayerSpireFields
{
    public static SpireField<Integer> totalBlockGained = new SpireField<Integer>(() -> 0);
    public static SpireField<Boolean> appliedDebuffThisTurn = new SpireField<>(() -> false);
}