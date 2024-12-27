package ruina.patches;

import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.Soul;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import ruina.cardmods.LieMod;
import ruina.cards.Lie;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.att;

public class LiePatches {
    @SpirePatch(clz = GameActionManager.class, method = "getNextAction")
    public static class LieSubvertPlay {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(GameActionManager __instance) {
            CardQueueItem cqi = __instance.cardQueue.get(0);
            AbstractCard c = cqi.card;
            if (c != null) {
                if (CardModifierManager.hasModifier(c, LieMod.ID)) {
                    if (__instance.cardQueue.get(0).monster == null) {
                        __instance.cardQueue.get(0).randomTarget = true;
                    }
                    AbstractDungeon.player.hand.removeCard(c);
                    LieMod mod = (LieMod) CardModifierManager.getModifiers(c, LieMod.ID).get(0);
                    AbstractDungeon.player.cardInUse = mod.source;
                    __instance.cardQueue.get(0).card = mod.source;
                    mod.source.current_x = c.current_x;
                    mod.source.current_y = c.current_y;
                    mod.source.target_x = Settings.WIDTH / 2F;
                    mod.source.target_y = Settings.HEIGHT / 2F;

                    mod.source.dontTriggerOnUseCard = !cqi.autoplayCard && !mod.source.canUse(AbstractDungeon.player, null);

                    att(new VFXActionButItCanFizzle(adp(), new WaitEffect(), 0.25f));
                    AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
                        @Override
                        public void update() {
                            isDone = true;
                            CardCrawlGame.sound.play("ATTACK_WHIFF_2");
                        }
                    });
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(GameActionManager.class, "usingCard");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = GameActionManager.class, method = "getNextAction")
    public static class CantAffordDontUse {
        public static boolean SERIOUSLY_NO = false;

        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(GameActionManager __instance) {
            AbstractCard c = __instance.cardQueue.get(0).card;
            if (c.cardID.equals(Lie.ID) && c.dontTriggerOnUseCard) {
                SERIOUSLY_NO = true;
                AbstractDungeon.actionManager.addToBottom(new UseCardAction(c));
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "useCard");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class SeriouslyDontUseIt {
        @SpirePrefixPatch
        public static SpireReturn Prefix(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
            if (CantAffordDontUse.SERIOUSLY_NO) {
                CantAffordDontUse.SERIOUSLY_NO = false;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            cls = "com.megacrit.cardcrawl.cards.Soul",
            method = "discard",
            paramtypes = {"com.megacrit.cardcrawl.cards.AbstractCard", "boolean"}
    )
    public static class disguiseInDiscardPile {
        @SpirePostfixPatch
        public static void disguise(Soul __instance, AbstractCard card, boolean visualOnly) {
            if (card instanceof Lie) {
                ((Lie) card).disguise();
            }
        }
    }

    @SpirePatch(
            cls = "com.megacrit.cardcrawl.cards.Soul",
            method = "onToDeck",
            paramtypes = {"com.megacrit.cardcrawl.cards.AbstractCard", "boolean", "boolean"}
    )
    public static class disguiseInDrawPile {
        @SpirePostfixPatch
        public static void disguise(Soul __instance, AbstractCard card, boolean randomSpot, boolean visualOnly) {
            if (card instanceof Lie) {
                ((Lie) card).disguise();
            }
        }
    }
}