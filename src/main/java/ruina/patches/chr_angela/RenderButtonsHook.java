package ruina.patches.chr_angela;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.ui.panels.DrawPilePanel;

import static ruina.RuinaMod.renderCombatUiElements;

@SpirePatch(
        clz = DrawPilePanel.class,
        method = "render"
)
public class RenderButtonsHook {
    public static void Postfix(DrawPilePanel __instance, SpriteBatch spriteBatch) {
        renderCombatUiElements(spriteBatch);
    }
}