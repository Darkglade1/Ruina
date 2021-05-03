package ruina.patches.chr_angela;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.unique.ApotheosisAction;
import com.megacrit.cardcrawl.cards.CardGroup;
import javassist.CtBehavior;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static ruina.RuinaMod.bookPile;

public class UpgradeBooksPatch {
    @SpirePatch(
            clz = ApotheosisAction.class,
            method = "update"
    )
    public static class ApotheosisActionPatch {
        @SpireInsertPatch(
                locator = ApotheosisActionPatchLocator.class
        )
        public static void Insert(ApotheosisAction _instance) {
            Method upgradeMethod;
            try {
                upgradeMethod = ApotheosisAction.class.getDeclaredMethod("upgradeAllCardsInGroup", CardGroup.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                return;
            }
            upgradeMethod.setAccessible(true);
            try {
                upgradeMethod.invoke(_instance, bookPile);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ApotheosisActionPatchLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher matcher = new Matcher.FieldAccessMatcher(ApotheosisAction.class, "isDone");
            return LineFinder.findInOrder(ctBehavior, matcher);
        }
    }

}