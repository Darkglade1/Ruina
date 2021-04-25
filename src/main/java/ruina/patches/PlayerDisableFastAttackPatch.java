package ruina.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import ruina.powers.PlayerBlackSilence;

import java.util.Iterator;

public class PlayerDisableFastAttackPatch {
    @SpirePatch(
            clz= AbstractCreature.class,
            method="useFastAttackAnimation"
    )
    public static class RenderPatch {
        public static SpireReturn Prefix(AbstractCreature __instance) {
            if (__instance instanceof AbstractPlayer && __instance.hasPower(PlayerBlackSilence.POWER_ID)) { return SpireReturn.Return(null); }
            return SpireReturn.Continue();
        }
    }
}