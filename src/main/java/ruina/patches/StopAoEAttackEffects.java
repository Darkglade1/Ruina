package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import ruina.monsters.AbstractAllyMonster;

import java.util.ArrayList;

import static ruina.util.Wiz.atb;

public class StopAoEAttackEffects {
    //stops AoE attack effects from showing on allies
    @SpirePatch(
            clz = DamageAllEnemiesAction.class,
            method = "update"
    )
    public static class GetNextAction {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(ArrayList.class.getName())
                            && m.getMethodName().equals("add")) {
                        m.replace("if (" + StopAoEAttackEffects.class.getName() + ".showEffect(" + AbstractDungeon.class.getName() + ".getCurrRoom().monsters.monsters.get(i))) {" +
                                "$_ = $proceed($$);" +
                                "}");
                    }
                }
            };
        }
    }

   public static boolean showEffect(Object mo) {
        if (mo instanceof AbstractAllyMonster) {
            AbstractAllyMonster ally = (AbstractAllyMonster)mo;
            return !(ally.isAlly && !ally.isTargetableByPlayer);
        } else {
            return true;
        }
   }

    @SpirePatch(
            clz = RelicAboveCreatureAction.class,
            method = "update"
    )
    public static class StopRelicAboveAllies {
        public static SpireReturn<Void> Prefix(RelicAboveCreatureAction __instance) {
            if (__instance.source instanceof AbstractAllyMonster) {
                AbstractAllyMonster ally = (AbstractAllyMonster)__instance.source;
                if (ally.isAlly && !ally.isTargetableByPlayer) {
                    __instance.isDone = true;
                    return SpireReturn.Return(null);
                }
            }
            return SpireReturn.Continue();
        }
    }
}