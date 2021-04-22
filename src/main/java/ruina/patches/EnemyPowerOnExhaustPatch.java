package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;
import ruina.powers.Emotion;

@SpirePatch(
        clz = CardGroup.class,
        method = "moveToExhaustPile",
        paramtypez={
                AbstractCard.class,
        }

)
// A patch to make Emotion work
public class EnemyPowerOnExhaustPatch {
    @SpireInsertPatch(locator = EnemyPowerOnExhaustPatch.Locator.class)
    public static void TriggerOnGainedBlock(CardGroup instance, AbstractCard c) {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            for (AbstractPower p : mo.powers) {
                if (p.ID.equals(Emotion.POWER_ID)) {
                    p.onExhaust(c);
                }
            }
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "triggerOnExhaust");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}