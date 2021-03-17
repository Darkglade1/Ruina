package ruina.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.powers.AbstractUnremovablePower;

@SpirePatch(clz = RemoveSpecificPowerAction.class, method = "update")
public class StopRemovingMyEnemyBuffsREEEEEEEEEEE {
	@SpirePrefixPatch()
	public static SpireReturn<Void> EveryTimeAnEnemyBuffsGetsRemovedIDieALittleInside(RemoveSpecificPowerAction instance) {
		AbstractPower powerBeingRemoved = ReflectionHacks.getPrivate(instance, RemoveSpecificPowerAction.class, "powerInstance");
		if (powerBeingRemoved instanceof AbstractUnremovablePower) {
			if (((AbstractUnremovablePower)powerBeingRemoved).isUnremovable) {
				instance.isDone = true;
				return SpireReturn.Return(null);
			}
		}
		return SpireReturn.Continue();
	}
}