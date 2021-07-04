package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.neow.NeowReward.NeowRewardDef;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.dungeons.AbstractRuinaDungeon;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;

public class NeowRewardPatches {
	
	@SpireEnum
	public static NeowReward.NeowRewardType RANDOM_EGO_CARD;
	@SpireEnum
	public static NeowReward.NeowRewardType CHOOSE_EGO_CARD;

	@SpirePatch(clz = NeowReward.class, method = "getRewardOptions")
	public static class RewardsPatch {
		public static ArrayList<NeowRewardDef> Postfix(ArrayList<NeowRewardDef> __result, NeowReward __instance, final int category) {
			UIStrings N_OPTION_STRINGS = CardCrawlGame.languagePack.getUIString(makeID("NeowOptions"));
			if (CardCrawlGame.dungeon instanceof AbstractRuinaDungeon) {
				ArrayList<NeowRewardDef> neowRewardToRemove = new ArrayList<>();
				for (NeowRewardDef neowReward : __result) {
					if (neowReward.type == NeowReward.NeowRewardType.RANDOM_COLORLESS || neowReward.type == NeowReward.NeowRewardType.RANDOM_COLORLESS_2) {
						neowRewardToRemove.add(neowReward);
					}
				}
				for (NeowRewardDef neowReward : neowRewardToRemove) {
					__result.remove(neowReward);
				}
				if (category == 0) {
					__result.add(new NeowRewardDef(RANDOM_EGO_CARD, N_OPTION_STRINGS.TEXT[0]));
				} else if (category == 2) {
					__result.add(new NeowRewardDef(CHOOSE_EGO_CARD, N_OPTION_STRINGS.TEXT[1]));
				}
			}
			return __result;
		}
	}
	
	@SpirePatch(clz = NeowReward.class, method = "activate")
	public static class ActivatePatch {
		public static void Postfix(NeowReward __instance) {
			UIStrings N_OPTION_STRINGS = CardCrawlGame.languagePack.getUIString(makeID("NeowOptions"));
			if (__instance.type == RANDOM_EGO_CARD) {
				AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(AbstractEgoCard.getNeowRandomEGOCard(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
			} else if (__instance.type == CHOOSE_EGO_CARD) {
				AbstractDungeon.cardRewardScreen.open(AbstractEgoCard.getNeowChooseEGOCard(), null, N_OPTION_STRINGS.TEXT[2]);
			}
		}
	}
}