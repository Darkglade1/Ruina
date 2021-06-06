package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;

public class DisableOtherGuysChallenge {
	@SpirePatch(cls = "lor_ex.room.LORVictoryRoom$goToVictoryRoomOrTheDoor", method = "prefix", optional = true)
	public static class WhyCouldntHeJustUseActLikeItLikeANormalPerson {
		public static Object Replace(ProceedButton instance) {
			return SpireReturn.Continue();
		}
	}

	@SpirePatch(cls = "lobotomyMod.event.BossRushEvent$goToVictoryRoomOrTheDoor", method = "prefix", optional = true)
	public static class WhyCouldntHeJustUseActLikeItLikeANormalPersonREEEE {
		public static Object Replace(ProceedButton instance) {
			return SpireReturn.Continue();
		}
	}
}