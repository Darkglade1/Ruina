package ruina.patches.chr_angela;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import javassist.CtBehavior;

import static ruina.RuinaMod.bookPile;

public class BookTriggerPatches {
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "applyStartOfTurnCards"
    )
    public static class TriggerAtTurnStartForFrozenPatch {
        public static void Postfix(AbstractPlayer _instance) {
            bookPile.group.forEach(c -> {
                if (c != null) {
                    c.atTurnStart();
                }
            });
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "switchedStance"
    )
    public static class TriggerSwitchedStancePatch {
        public static void Postfix(AbstractPlayer _instance) {
            bookPile.group.forEach(AbstractCard::switchedStance);
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "updateCardsOnDamage"
    )
    public static class TriggerCardsOnDamagePatch {
        public static void Postfix(AbstractPlayer _instance) {
            bookPile.group.forEach(AbstractCard::tookDamage);
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "updateCardsOnDiscard"
    )
    public static class TriggerCardsOnDiscardPatch {
        public static void Postfix(AbstractPlayer _instance) {
            bookPile.group.forEach(AbstractCard::didDiscard);
        }
    }

    @SpirePatch(
            clz = GameActionManager.class,
            method = "getNextAction"
    )
    public static class TriggerCardsOnPlayCardPatch {
        @SpireInsertPatch(
                locator = TriggerCardsOnPlayCardPatchLocator.class
        )
        public static void Insert(GameActionManager _instance) {
            if (bookPile != null && bookPile.size() > 0) {
                CardQueueItem item = _instance.cardQueue.get(0);
                bookPile.group.forEach(c -> c.onPlayCard(item.card, item.monster));
            }
        }
    }

    private static class TriggerCardsOnPlayCardPatchLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher matcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "cardsPlayedThisTurn");
            return LineFinder.findInOrder(ctBehavior, matcher);
        }
    }

    @SpirePatch(
            clz = UseCardAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, AbstractCreature.class}
    )
    public static class TriggerCardsTriggerOnCardPlayedPatch {
        @SpireInsertPatch(
                locator = TriggerCardsTriggerOnCardPlayedPatchLocator.class
        )
        public static void Insert(UseCardAction _instance, AbstractCard card, AbstractCreature target) {
            if (bookPile != null && bookPile.size() > 0) {
                if (!card.dontTriggerOnUseCard) { bookPile.group.forEach(c -> c.triggerOnCardPlayed(card)); }
            }
        }
    }

    private static class TriggerCardsTriggerOnCardPlayedPatchLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher matcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "drawPile");
            return LineFinder.findInOrder(ctBehavior, matcher);
        }
    }
}