package ruina.patches;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.audio.MainMusic;
import javassist.CtBehavior;
import ruina.dungeons.AbstractRuinaDungeon;

import java.util.ArrayList;

@SpirePatch(clz = CustomDungeon.class, method = "setupMisc")
public class CustomDungeonMusicPatch {
    @SpireInsertPatch(locator = CustomDungeonMusicPatch.Locator.class, localvars = {"tracks"})
    public static SpireReturn<Void> play(CustomDungeon instance, ArrayList<MainMusic> tracks) {
        if (instance instanceof AbstractRuinaDungeon) {
            ((AbstractRuinaDungeon) instance).setFloor();
            Music music = MainMusic.newMusic(instance.mainmusic);
            MainMusic main = new MainMusic(instance.id);
            ReflectionHacks.setPrivate(main, MainMusic.class, "music", music);
            tracks.add(main);
            music.setLooping(true);
            music.play();
            music.setVolume(0.0F);
            return SpireReturn.Return(null);
        } else {
            return SpireReturn.Continue();
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(CustomDungeon.class, "mainmusic");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}