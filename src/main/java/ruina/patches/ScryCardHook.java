package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ScryCardHook {
    public static Consumer<ArrayList<AbstractCard>> callback;

    @SpirePatch(clz = ScryAction.class, method = "update")
    public static class GetScryedCards {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(ScryAction __instance) {
            if (callback != null) {
                callback.accept(AbstractDungeon.gridSelectScreen.selectedCards);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "clear");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }
}