package ruina.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.shop.ShopScreen;
import javassist.CtBehavior;
import ruina.cards.EGO.AbstractEgoCard;

@SpirePatch(
        clz = ShopScreen.class,
        method = "purchaseCard"
)
// A patch to prevent crash when buying ego cards with Courier
public class FixCourierCrash {
    @SpireInsertPatch(locator = Locator.class)
    public static SpireReturn<Void> FixCourier(ShopScreen instance, AbstractCard hoveredCard) {
        if (hoveredCard instanceof AbstractEgoCard) {
            AbstractCard card = AbstractEgoCard.getRandomEgoCards(1).get(0);
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onPreviewObtainCard(card);
            }
            card.current_x = hoveredCard.current_x;
            card.current_y = hoveredCard.current_y;
            card.target_x = card.current_x;
            card.target_y = card.current_y;
            ReflectionHacks.privateMethod(ShopScreen.class, "setPrice", AbstractCard.class).invoke(instance, card);
            card.price *= 1.2F; //manually add the cost increase for colorless cards
            instance.colorlessCards.set(instance.colorlessCards.indexOf(hoveredCard), card);
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "getCardFromPool");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}