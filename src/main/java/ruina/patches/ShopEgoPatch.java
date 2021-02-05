package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.shop.Merchant;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CtBehavior;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.dungeons.AbstractRuinaDungeon;

import java.util.ArrayList;

@SpirePatch(
        clz = Merchant.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez =  { float.class, float.class, int.class }

)
// A patch to make the shop sell ego cards instead of colorless cards
public class ShopEgoPatch {
    @SpireInsertPatch(locator = Locator.class, localvars = {"cards2"})
    public static void SellEgo(Merchant __instance, float x, float y, int newShopScreen, ArrayList<AbstractCard> cards2) {
        if (CardCrawlGame.dungeon instanceof AbstractRuinaDungeon) {
            ArrayList<String> egoCards = AbstractEgoCard.getRandomEgoCards(2);
            cards2.clear();
            for (String egoID : egoCards) {
                AbstractCard egoCard = CardLibrary.getCard(egoID).makeCopy();
                cards2.add(egoCard);
                UnlockTracker.unlockCard(egoID);
            }
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ShopScreen.class, "init");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}