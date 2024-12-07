package ruina.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class RenderPowerTipsPatch {

    @SpirePatch(
            clz = AbstractMonster.class,
            method = "renderTip",
            paramtypez = {
                    SpriteBatch.class,
            }
    )
    public static class DontRenderTipMonster {
        @SpirePrefixPatch()
        public static SpireReturn<Void> dont(AbstractMonster instance, SpriteBatch sb) {
            if (RenderHandPatch.plsDontRenderHandOrTips) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "renderPowerTips",
            paramtypez = {
                    SpriteBatch.class,
            }
    )
    public static class DontRenderTipPlayer {
        @SpirePrefixPatch()
        public static SpireReturn<Void> dont(AbstractPlayer instance, SpriteBatch sb) {
            if (RenderHandPatch.plsDontRenderHandOrTips) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }


}