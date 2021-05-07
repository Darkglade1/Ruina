package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MinionPower;
import javassist.CtBehavior;
import ruina.monsters.angela.AbnormalityContainer;
import ruina.powers.SuppressionPower;

import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.monsterList;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "die",
        paramtypez = {boolean.class}
)
public class OnEnemyDeath {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void triggerOnDeathPowers(AbstractMonster __instance, boolean triggerRelics) {
        //check for minions

        for (AbstractPower q : AbstractDungeon.player.powers) { if (q instanceof SuppressionPower) ((SuppressionPower) q).onSuppression(__instance); }

        for (AbstractMonster t : monsterList()) {
            for (AbstractPower r : t.powers) {
                if (r instanceof SuppressionPower) ((SuppressionPower) r).onSuppression(__instance);
            }
        }

        boolean allMinion = true;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped() && !m.hasPower(MinionPower.POWER_ID)) {
                allMinion = false;
                break;
            }
        }

        if (allMinion) {
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                if (!m.isDeadOrEscaped() && (m instanceof AbnormalityContainer)) atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        ((AbnormalityContainer) m).properDie();
                        isDone = true;
                    }
                });
            }
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