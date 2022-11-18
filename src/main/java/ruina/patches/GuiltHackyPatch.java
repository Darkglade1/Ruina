package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.CtBehavior;
import ruina.cards.Guilt;


// A patch to fix shit for Guilt so that it can play itself out of hand when triggering its effect
// but without exhausting itself. So it sets exhaust to false at initial play and gets exhaust set back to true here.
public class GuiltHackyPatch {

    @SpirePatch(
            clz = UseCardAction.class,
            method = "update"
    )
    public static class FixCardExhaust {
        @SpireInsertPatch(locator = FixCardExhaust.Locator.class, localvars = {"targetCard"})
        public static void HackyCardExhaustChange(UseCardAction instance, @ByRef AbstractCard[] targetCard) {
            if (targetCard[0] instanceof Guilt) {
                targetCard[0].exhaust = true;
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.FieldAccessMatcher fieldAccessMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "freeToPlayOnce");
                return LineFinder.findInOrder(ctMethodToPatch, fieldAccessMatcher);
            }
        }
    }


}