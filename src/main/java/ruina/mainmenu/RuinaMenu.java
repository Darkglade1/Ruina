package ruina.mainmenu;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.audio.MainMusic;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.scenes.TitleBackground;
import com.megacrit.cardcrawl.scenes.TitleCloud;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.*;

public class RuinaMenu {

    @SpirePatch(clz = TitleBackground.class, method = SpirePatch.CONSTRUCTOR)
    public static class TitleBackgroundReplacementPatch {

        @SpirePostfixPatch
        public static void RuinaTitle(TitleBackground __instance) {
            if(altReverbClear) {
                TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(makeUIPath("mainmenu/title.atlas")));
                setTitleBackgroundAtlasRegion(__instance, atlas, "sky", "jpg/sky");
                setTitleBackgroundAtlasRegion(__instance, atlas, "mg3Bot", "mg3Bot");
                setTitleBackgroundAtlasRegion(__instance, atlas, "mg3Top", "mg3Top");
                setTitleBackgroundAtlasRegion(__instance, atlas, "topGlow", "mg3TopGlow1");
                setTitleBackgroundAtlasRegion(__instance, atlas, "topGlow2", "mg3TopGlow2");
                setTitleBackgroundAtlasRegion(__instance, atlas, "botGlow", "mg3BotGlow");

                ArrayList<TitleCloud> newTopClouds = new ArrayList<>();
                ArrayList<TitleCloud> newMidClouds = new ArrayList<>();

                for (int i = 0; i < 6; i++) {
                    newTopClouds.add(new TitleCloud(
                            atlas.findRegion("topCloud" + (i + 1)),
                            MathUtils.random(10.0f, 50.0f) * Settings.scale,
                            MathUtils.random(-1920.0f, 1920.0f) * Settings.scale)
                    );
                }

                for (int i = 0; i < 12; i++) {
                    newTopClouds.add(new TitleCloud(
                            atlas.findRegion("midCloud" + (i + 1)),
                            MathUtils.random(-50.0f, -10.0f) * Settings.scale,
                            MathUtils.random(-1920.0f, 1920.0f) * Settings.scale)
                    );
                }

                ReflectionHacks.setPrivate(__instance, TitleBackground.class, "topClouds", newTopClouds);
                ReflectionHacks.setPrivate(__instance, TitleBackground.class, "midClouds", newMidClouds);
            }
        }
    }

    @SpirePatch(clz = MainMusic.class, method = "getSong")
    public static class MusicHijack {
        @SpirePostfixPatch
        public static Music Postfix(Music __result, MainMusic __instance, String key) {
            if (altReverbClear) { if (key.equals("MENU")) { return MainMusic.newMusic("audio/music/m1.ogg"); } }
            return __result;
        }
    }

    private static void setTitleBackgroundAtlasRegion(TitleBackground menu, TextureAtlas newAtlas, String classVarName, String srcRegionName) { ReflectionHacks.setPrivate(menu, TitleBackground.class, classVarName, newAtlas.findRegion(srcRegionName)); }

}
