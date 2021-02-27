package ruina.patches;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;
import ruina.monsters.act3.BurrowingHeaven;

@SpirePatch(
        clz = AbstractCreature.class,
        method = "addBlock",
        paramtypez={
                int.class,
        }

)
// A patch to make Unnerving work
public class EnemyPowerPlayerBlockPatch {
    @SpireInsertPatch(locator = EnemyPowerPlayerBlockPatch.Locator.class, localvars = {"tmp"})
    public static void TriggerOnGainedBlock(AbstractCreature instance, int blockAmount, @ByRef float[] tmp) {
        if (instance.isPlayer) {
            if (tmp[0] > 0.0F) {
                for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    for (AbstractPower p : mo.powers) {
                        if (p.ID.equals(BurrowingHeaven.POWER_ID)) {
                           p.onGainedBlock((int)tmp[0]);
                        }
                    }
                }
            }
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(MathUtils.class, "floor");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}