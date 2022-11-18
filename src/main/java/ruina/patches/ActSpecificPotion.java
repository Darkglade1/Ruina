package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.DuplicationPotion;
import com.megacrit.cardcrawl.potions.LiquidBronze;
import com.megacrit.cardcrawl.random.Random;
import ruina.dungeons.AbstractRuinaDungeon;
import ruina.potions.EgoPotion;

public class ActSpecificPotion {

    @SpirePatch(
            clz = PotionHelper.class,
            method = "getRandomPotion",
            paramtypez = {
                    Random.class
            }
    )
    public static class patch1 {
        @SpirePostfixPatch
        public static AbstractPotion Postfix(AbstractPotion original, Random rng) {
            if (AbstractDungeon.player != null && original instanceof EgoPotion && !(CardCrawlGame.dungeon instanceof AbstractRuinaDungeon)) {
                System.out.println("Preventing Ruina potion from spawning in non-Ruina Act, spawning Dupe potion instead");
                return new DuplicationPotion();
            } else {
                return original;
            }
        }
    }

    @SpirePatch(
            clz = PotionHelper.class,
            method = "getRandomPotion",
            paramtypez = {}
    )
    public static class patch2 {
        @SpirePostfixPatch
        public static AbstractPotion Postfix(AbstractPotion original) {
            if (AbstractDungeon.player != null && original instanceof EgoPotion && !(CardCrawlGame.dungeon instanceof AbstractRuinaDungeon)) {
                System.out.println("Preventing Ruina potion from spawning in non-Ruina Act, spawning Dupe potion instead");
                return new DuplicationPotion();
            } else {
                return original;
            }
        }
    }

    @SpirePatch(
            clz = PotionHelper.class,
            method = "getRandomPotion",
            paramtypez = {
                    Random.class
            }
    )
    public static class patch3 {
        @SpirePostfixPatch
        public static AbstractPotion Postfix(AbstractPotion original, Random rng) {
            if (AbstractDungeon.player != null && (original.ID.equals("bundle_of_potions:PickleJuiceUncommon") || original.ID.equals("bundle_of_potions:FungiinaBottleUncommon"))) {
                return new LiquidBronze();
            } else {
                return original;
            }
        }
    }

    @SpirePatch(
            clz = PotionHelper.class,
            method = "getRandomPotion",
            paramtypez = {}
    )
    public static class patch4 {
        @SpirePostfixPatch
        public static AbstractPotion Postfix(AbstractPotion original) {
            if (AbstractDungeon.player != null && (original.ID.equals("bundle_of_potions:PickleJuiceUncommon") || original.ID.equals("bundle_of_potions:FungiinaBottleUncommon"))) {
                return new LiquidBronze();
            } else {
                return original;
            }
        }
    }

}