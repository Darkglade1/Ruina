package ruina.metrics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.metrics.Metrics;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.io.Serializable;
import java.util.HashMap;

public class RuinaMetricsPatches {

    @SpirePatch(clz = Metrics.class, method = "sendPost", paramtypez = {String.class, String.class})
    public static class SendPostPatch {
        @SpireInsertPatch(rloc = 1, localvars = {"event"})
        public static void patch(Metrics __instance, @ByRef String[] url, String fTL, HashMap<String, Serializable> event) {
            if(__instance instanceof RuinaMetrics) {
                url[0] = RuinaMetrics.url;
            }
        }

    }

    @SpirePatch(clz = VictoryScreen.class, method = "submitVictoryMetrics")
    public static class RunRuinaMetricsOnVictory {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(VictoryScreen __instance) {
            if (Settings.UPLOAD_DATA) {
                Metrics metrics = new RuinaMetrics();
                metrics.setValues(false, true, null, Metrics.MetricRequestType.UPLOAD_METRICS);
                Thread t = new Thread(metrics);
                t.start();
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Settings.class, "isStandardRun");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = DeathScreen.class, method = "submitDefeatMetrics")
    public static class RunRuinaMetricsOnDefeat {
        @SpirePostfixPatch
        public static void patch(DeathScreen __insatnce, MonsterGroup m) {
            if (Settings.UPLOAD_DATA && (AbstractDungeon.actNum > 1 || AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.getCurrRoom() != null && (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss || AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite))) {
                Metrics metrics = new RuinaMetrics();
                metrics.setValues(true, false, m, Metrics.MetricRequestType.UPLOAD_METRICS);
                Thread t = new Thread(metrics);
                t.start();
            }
        }
    }

    @SpirePatch(clz = DeathScreen.class, method = "submitVictoryMetrics")
    public static class RunRuinaMetricsOnDefeatButVictoryActually {
        @SpireInsertPatch(locator = RunRuinaMetricsOnVictory.Locator.class)
        public static void patch(DeathScreen __instance) {
            if (Settings.UPLOAD_DATA) {
                Metrics metrics = new RuinaMetrics();
                metrics.setValues(false, false, null, Metrics.MetricRequestType.UPLOAD_METRICS);
                Thread t = new Thread(metrics);
                t.start();
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Settings.class, "isStandardRun");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = {String.class, AbstractPlayer.class})
    public static class CollectActNames {
        @SpirePostfixPatch
        public static void patch(CardCrawlGame __instance, String key, AbstractPlayer p) {
            if(AbstractDungeon.floorNum <= 0) {
                RuinaMetrics.acts.clear();
            }
            RuinaMetrics.acts.add(AbstractDungeon.id);
        }
    }
}