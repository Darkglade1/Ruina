package ruina.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.InvinciblePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act5.AClaw;
import ruina.powers.AbstractUnremovablePower;

@SpirePatch(clz = RemoveSpecificPowerAction.class, method = "update")
public class StopRemovingMyEnemyBuffsREEEEEEEEEEE {
	@SpirePrefixPatch()
	public static SpireReturn<Void> EveryTimeAnEnemyBuffsGetsRemovedIDieALittleInside(RemoveSpecificPowerAction instance) {
		AbstractPower powerBeingRemoved = ReflectionHacks.getPrivate(instance, RemoveSpecificPowerAction.class, "powerInstance");
		String powerToRemove = ReflectionHacks.getPrivate(instance, RemoveSpecificPowerAction.class, "powerToRemove");
		if (powerBeingRemoved == null) {
			powerBeingRemoved = instance.target.getPower(powerToRemove);
		}
		if (powerBeingRemoved instanceof AbstractUnremovablePower && instance.target instanceof AbstractMonster) {
			if (((AbstractUnremovablePower)powerBeingRemoved).isUnremovable && powerBeingRemoved.type == AbstractPower.PowerType.BUFF) {
				instance.isDone = true;
				return SpireReturn.Return(null);
			}
		}

		//stop removing strength from Baral reeeee
		if (powerBeingRemoved instanceof StrengthPower && instance.target.hasPower(AClaw.POWER_ID)) {
			instance.isDone = true;
			return SpireReturn.Return(null);
		}

		if (powerBeingRemoved instanceof InvinciblePower && instance.target instanceof AbstractRuinaMonster) {
			instance.isDone = true;
			return SpireReturn.Return(null);
		}
		return SpireReturn.Continue();
	}
}