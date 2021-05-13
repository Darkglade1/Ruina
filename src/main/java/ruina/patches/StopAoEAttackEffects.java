package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import ruina.monsters.AbstractAllyMonster;

import java.util.ArrayList;

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
}