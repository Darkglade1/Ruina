package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import ruina.dungeons.AbstractRuinaDungeon;

@SpirePatch(clz = CampfireUI.class, method = "updateFire")
public class CampfireFirePatch {
	public static SpireReturn Prefix(CampfireUI __instance) {
		if (CardCrawlGame.dungeon instanceof AbstractRuinaDungeon) {
			return SpireReturn.Return(null);
		}
		return SpireReturn.Continue();
	}
}