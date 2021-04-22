package ruina.mainmenu;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.vfx.scene.TitleDustEffect;

import static ruina.RuinaMod.blacksilenceClear;
import static ruina.RuinaMod.makeUIPath;

public class DustPatch {

    @SpirePatch(clz = TitleDustEffect.class, method = SpirePatch.CONSTRUCTOR)
    public static class TitleDustGoodbye {

        @SpirePostfixPatch
        public static void JustMonikaNoDust(TitleDustEffect __instance) {
            if(blacksilenceClear) {
                TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(makeUIPath("mainmenu/dust.atlas")));
                ReflectionHacks.setPrivate(__instance, TitleDustEffect.class, "img", atlas.findRegion("dust"));
            }
        }
    }
}