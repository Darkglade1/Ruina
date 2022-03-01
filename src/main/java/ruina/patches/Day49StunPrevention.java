package ruina.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.monsters.day49.Act1Angela;
import ruina.powers.AClaw;
import ruina.powers.AbstractUnremovablePower;

@SpirePatch(clz = ApplyPowerAction.class, method = "update")
public class Day49StunPrevention {
	@SpirePrefixPatch()
	public static SpireReturn<Void> RefrainFromNegatingMyMechanicsPlease(ApplyPowerAction instance) {
		AbstractPower appliedPower = ReflectionHacks.getPrivate(instance, ApplyPowerAction.class, "powerToApply");
		if(appliedPower instanceof StunMonsterPower && isAngela(instance.target)){
			instance.isDone = true;
			return SpireReturn.Return(null);
		}
		return SpireReturn.Continue();
	}

	public static boolean isAngela(AbstractCreature m){
		return m instanceof Act1Angela;
	}
}