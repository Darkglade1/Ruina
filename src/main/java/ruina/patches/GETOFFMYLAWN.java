package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import ruina.dungeons.AbstractRuinaDungeon;

public class GETOFFMYLAWN {

	@SpirePatch(cls = "lor_ex.relic.ShadeInBook$getMonsterForRoomCreation", method = "Insert", optional = true)
	public static class GOAWAY {
		@SpirePostfixPatch()
		public static SpireReturn<MonsterGroup> PLEASE (SpireReturn<MonsterGroup> original, AbstractDungeon _inst) {
			if (CardCrawlGame.dungeon instanceof AbstractRuinaDungeon) {
				return SpireReturn.Continue();
			} else {
				return original;
			}
		}
	}

	@SpirePatch(cls = "lor_ex.relic.ShadeInBook$getEliteMonsterForRoomCreation", method = "Insert", optional = true)
	public static class GOAWAY2 {
		@SpirePostfixPatch()
		public static SpireReturn<MonsterGroup> PLEASE (SpireReturn<MonsterGroup> original, AbstractDungeon _inst) {
			if (CardCrawlGame.dungeon instanceof AbstractRuinaDungeon) {
				return SpireReturn.Continue();
			} else {
				return original;
			}
		}
	}
}