package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import javassist.CtBehavior;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.act2.BadWolf;
import ruina.monsters.theHead.Zena;

@SpirePatch(
        clz = PotionPopUp.class,
        method = "updateTargetMode"

)
// A patch to make allies untargetable by POTIONS
public class MakeAlliesUntargetableByPOTIONS {
    @SpireInsertPatch(locator = Locator.class, localvars = {"hoveredMonster"})
    public static void MakeHoveredMonsterNull(PotionPopUp instance, @ByRef AbstractMonster[] hoveredMonster) {
        if (hoveredMonster[0] instanceof AbstractAllyMonster) {
            AbstractAllyMonster ally = (AbstractAllyMonster)hoveredMonster[0];
            if (ally.isAlly && !ally.isTargetableByPlayer) {
                hoveredMonster[0] = null;
            }
        }

        if (hoveredMonster[0] != null && hoveredMonster[0].hasPower(BadWolf.SKULK_POWER_ID)) {
            hoveredMonster[0] = null;
        }

        if (hoveredMonster[0] != null && hoveredMonster[0] instanceof Zena && hoveredMonster[0].halfDead) {
            hoveredMonster[0] = null;
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(InputHelper.class, "justClickedLeft");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}