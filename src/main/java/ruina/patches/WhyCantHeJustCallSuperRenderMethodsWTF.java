package ruina.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import ruina.powers.PlayerBlackSilence;

import java.util.Iterator;

public class WhyCantHeJustCallSuperRenderMethodsWTF {
    @SpirePatch2(
            cls = "lor.character.Librarian",
            method="render",
            optional = true
    )
    public static class USE_SUPER_RENDER_PLEASE {
        public static SpireReturn Prefix(AbstractPlayer __instance, SpriteBatch sb) {
            if (AbstractDungeon.player.hasPower(PlayerBlackSilence.POWER_ID)) {
                System.out.print("gamers");
                __instance.renderHealth(sb);
                if (!__instance.orbs.isEmpty()) {
                    Iterator var2 = __instance.orbs.iterator();
                    while (var2.hasNext()) {
                        AbstractOrb o = (AbstractOrb) var2.next();
                        o.render(sb);
                    }
                }
                __instance.hb.render(sb);
                __instance.healthHb.render(sb);
                return SpireReturn.Return(null);
            } else {
                return SpireReturn.Continue();
            }
        }
    }


}