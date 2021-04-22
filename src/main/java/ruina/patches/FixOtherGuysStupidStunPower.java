package ruina.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;

import static ruina.util.Wiz.att;

public class FixOtherGuysStupidStunPower {

	@SpirePatch(clz = ApplyPowerAction.class, method = "update")
	public static class ApplyPowersPatch {
		@SpirePrefixPatch()
		public static SpireReturn<Void> StopCrashingMyEnemiesREEEEEEEEE(ApplyPowerAction instance) {
			AbstractPower powerBeingApplied = ReflectionHacks.getPrivate(instance, ApplyPowerAction.class, "powerToApply");
			if (powerBeingApplied.ID.equals("StunPower")) {
				instance.isDone = true;
				if (instance.target instanceof AbstractMonster) {
					att(new StunMonsterAction((AbstractMonster) instance.target, instance.source));
				}
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}
	}

	@SpirePatch(cls = "lor.power.StunPower", method = SpirePatch.CONSTRUCTOR, optional = true)
	public static class WhyDoesThisShitDoItsStuffInCONSTRUCTOR {
		@SpireInsertPatch(locator = WhyDoesThisShitDoItsStuffInCONSTRUCTOR.Locator.class)
		public static SpireReturn<Void> FuckThisThing() {
			return SpireReturn.Return(null);
		}

		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "setMove");
				return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
			}
		}
	}
}