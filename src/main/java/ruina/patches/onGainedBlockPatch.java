package ruina.patches;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import javassist.CtBehavior;

import static ruina.util.Wiz.adp;

@SpirePatch(
        clz = AbstractCreature.class,
        method = "addBlock",
        paramtypez={
                int.class,
        }
)
// A patch to keep track of total block gained each combat
public class onGainedBlockPatch {
    @SpireInsertPatch(locator = onGainedBlockPatch.Locator.class, localvars = {"tmp"})
    public static void TriggerOnGainedBlock(AbstractCreature instance, int blockAmount, @ByRef float[] tmp) {
        if (instance.isPlayer) {
            if (tmp[0] > 0.0F) {
                int totalBlockGained = PlayerSpireFields.totalBlockGained.get(adp());
                PlayerSpireFields.totalBlockGained.set(adp(), totalBlockGained + blockAmount);
            }
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(MathUtils.class, "floor");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}