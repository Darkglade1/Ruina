package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;
import ruina.monsters.AbstractAllyMonster;

@SpirePatch(
        clz = GameActionManager.class,
        method = "getNextAction"

)
// A patch to make allies halfdead at the start of the player's turn
public class MakeAlliesHalfDead {
    @SpireInsertPatch(locator = Locator.class)
    public static void MakeAlliesHalfDeadReee(GameActionManager instance) {
        if (AbstractDungeon.getCurrRoom() != null) {
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDead) {
                    if (mo instanceof AbstractAllyMonster) {
                        AbstractAllyMonster ally = (AbstractAllyMonster)mo;
                        if (ally.isAlly) {
                            AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                                @Override
                                public void update() {
                                    mo.halfDead = true;
                                    this.isDone = true;
                                }
                            });
                        }
                    }
                }
            }
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "applyStartOfTurnRelics");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}