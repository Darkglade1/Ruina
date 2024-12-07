package ruina.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

@SpirePatch(cls = "com.megacrit.cardcrawl.characters.AbstractPlayer", method = "renderHand")
public class RenderHandPatch {
	public static boolean plsDontRenderHandOrTips = false;
	public static SpireReturn<Void> Prefix(final AbstractPlayer __instance, final SpriteBatch sb) {
		if (plsDontRenderHandOrTips) {
			return SpireReturn.Return(null);
		}
		return SpireReturn.Continue();
	}
}