package ruina.mainmenu;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.audio.MainMusic;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.scenes.TitleBackground;
import com.megacrit.cardcrawl.scenes.TitleCloud;
import com.megacrit.cardcrawl.vfx.scene.LogoFlameEffect;
import ruina.RuinaMod;

import java.util.ArrayList;

import static ruina.RuinaMod.makeUIPath;

public class RuinaMenu {

    @SpirePatch(clz = TitleBackground.class, method = SpirePatch.CONSTRUCTOR)
    public static class TitleBackgroundReplacementPatch {

        @SpirePostfixPatch
        public static void RuinaTitle(TitleBackground __instance) {
            if(RuinaMod.clownTime()) {
                TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(makeUIPath("mainmenu/title.atlas")));
                setTitleBackgroundAtlasRegion(__instance, atlas, "sky", "jpg/clown");
                setTitleBackgroundAtlasRegion(__instance, atlas, "mg3Bot", "mg3Bot");
                setTitleBackgroundAtlasRegion(__instance, atlas, "mg3Top", "mg3Top");
                setTitleBackgroundAtlasRegion(__instance, atlas, "topGlow", "mg3TopGlow1");
                setTitleBackgroundAtlasRegion(__instance, atlas, "topGlow2", "mg3TopGlow2");
                setTitleBackgroundAtlasRegion(__instance, atlas, "botGlow", "mg3BotGlow");
                ReflectionHacks.setPrivate(__instance, TitleBackground.class, "titleLogoImg", new Texture(makeUIPath("mainmenu/emptyTitle.png")));
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
            else if(RuinaMod.hijackMenu()) {
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
            if (RuinaMod.clownTime()) { if (key.equals("MENU")) { return MainMusic.newMusic("audio/music/clown.ogg"); } }
            else if (RuinaMod.hijackMenu()) { if (key.equals("MENU")) { return MainMusic.newMusic("audio/music/m1.ogg"); } }
            return __result;
        }
    }

    private static void setTitleBackgroundAtlasRegion(TitleBackground menu, TextureAtlas newAtlas, String classVarName, String srcRegionName) { ReflectionHacks.setPrivate(menu, TitleBackground.class, classVarName, newAtlas.findRegion(srcRegionName)); }

    @SpirePatch2(clz = LogoFlameEffect.class,
            method = "render",
            paramtypez={
                    SpriteBatch.class,
                    float.class, float.class
            }
    )
    public static class GoodbyeFlames {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(LogoFlameEffect __instance, SpriteBatch sb, float x, float y) {
            if (RuinaMod.clownTime()) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
