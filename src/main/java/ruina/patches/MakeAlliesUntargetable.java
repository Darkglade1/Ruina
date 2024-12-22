package ruina.patches;

import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import javassist.CtBehavior;
import ruina.cardmods.BrainwashMod;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.theHead.Zena;
import ruina.monsters.uninvitedGuests.normal.clown.Tiph;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "updateSingleTargetInput"

)
// A patch to make allies untargetable by the player
public class MakeAlliesUntargetable {
    @SpireInsertPatch(locator = Locator.class, localvars = {"hoveredMonster"})
    public static void MakeHoveredMonsterNull(AbstractPlayer instance, @ByRef AbstractMonster[] hoveredMonster) {
        if (AbstractDungeon.player.hoveredCard != null && CardModifierManager.hasModifier(AbstractDungeon.player.hoveredCard, BrainwashMod.ID)) {
            for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                if (mo instanceof Tiph) {
                    ((Tiph) mo).isTargetableByPlayer = true;
                    mo.halfDead = false;
                    break;
                }
            }
            if (hoveredMonster[0] != null && !(hoveredMonster[0] instanceof Tiph)) {
                hoveredMonster[0] = null;
            }
        }
        if (hoveredMonster[0] instanceof AbstractAllyMonster) {
            AbstractAllyMonster ally = (AbstractAllyMonster)hoveredMonster[0];
            if (ally.isAlly && !ally.isTargetableByPlayer) {
                hoveredMonster[0] = null;
            }
        }
        if (hoveredMonster[0] != null && hoveredMonster[0] instanceof Zena && hoveredMonster[0].halfDead) {
            hoveredMonster[0] = null;
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(MonsterGroup.class, "areMonstersBasicallyDead");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}