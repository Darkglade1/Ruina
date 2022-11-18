package ruina.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.PreservedInsect;
import ruina.monsters.AbstractAllyMonster;

import static ruina.util.Wiz.att;

public class SaveAlliesFromPreservedInsect {
    @SpirePatch(
            clz = PreservedInsect.class,
            method = "atBattleStart"
    )
    public static class DontTouchMyAllies {
        public static SpireReturn Prefix(PreservedInsect __instance) {
            float modAmt = ReflectionHacks.getPrivate(__instance, PreservedInsect.class, "MODIFIER_AMT");
            if (AbstractDungeon.getCurrRoom().eliteTrigger) {
                __instance.flash();
                for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!(mo instanceof AbstractAllyMonster)) {
                        mo.currentHealth = (int)((float)mo.currentHealth * (1.0F - modAmt));
                        mo.healthBarUpdatedEvent();
                    }
                }
                att(new RelicAboveCreatureAction(AbstractDungeon.player, __instance));
            }
            return SpireReturn.Return(null);
        }
    }
}
