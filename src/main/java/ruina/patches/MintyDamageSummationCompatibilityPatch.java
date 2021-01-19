package ruina.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.util.AdditionalIntent;

@SpirePatch(cls = "mintySpire.patches.ui.RenderIncomingDamagePatches$TIDHook", method = "hook", optional = true)
public class MintyDamageSummationCompatibilityPatch {
	@SpireInsertPatch(locator = MintyDamageSummationCompatibilityPatch.Locator.class, localvars = {"dmg", "c", "m"})
	public static void patch(AbstractDungeon fakeInstance, SpriteBatch sb, @ByRef int[] dmg, @ByRef int[] c, AbstractMonster m) {
		if (m instanceof AbstractMultiIntentMonster) {
			AbstractMultiIntentMonster multiIntentMonster = (AbstractMultiIntentMonster)m;
			for (AdditionalIntent additionalIntent : multiIntentMonster.additionalIntents) {
				if (additionalIntent.targetTexture == null && additionalIntent.damage > 0) {
					int damageSum;
					if (additionalIntent.numHits > 0) {
						damageSum = additionalIntent.damage * additionalIntent.numHits;
					} else {
						damageSum = additionalIntent.damage;
					}
					c[0]++;
					dmg[0] += damageSum;
				}
			}
			//stop minty from double counting the highest intent if the enemy isn't attacking
			//with their main intent
			if (multiIntentMonster.getRealIntentBaseDmg() < 0) {
				dmg[0] -= multiIntentMonster.getIntentDmg();
			}
		}
	}

	private static class Locator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "getIntentDmg");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}