package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;
import ruina.powers.AbstractEasyPower;

import static ruina.util.Wiz.monsterList;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "draw",
        paramtypez={
                int.class,
        }
)
// A patch to make onCardDraw hook trigger on monster powers
public class MonsterOnCardDrawPatch {
    @SpireInsertPatch(locator = MonsterOnCardDrawPatch.Locator.class, localvars = {"c"})
    public static void TriggerMonsterPower(AbstractPlayer instance, int numCards, AbstractCard c) {
        for (AbstractMonster mo : monsterList()) {
            for (AbstractPower p : mo.powers) {
                //hardcode this so we don't accidentally break other mods
                if (p instanceof AbstractEasyPower) {
                    p.onCardDraw(c);
                }
            }
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}