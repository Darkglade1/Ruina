package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.random.Random;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import ruina.monsters.AbstractAllyMonster;

//Stop allies from getting targetted by random effects (esp by stuff like static discharge triggering on enemy turn
public class RandomHitNotAllies {

    @SpirePatch(
            clz = MonsterGroup.class,
            method = "getRandomMonster",
            paramtypez = {
                    AbstractMonster.class,
                    boolean.class,
                    Random.class
            }
    )
    public static class RandomPlease {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("add")) {
                        m.replace("{" +
                                "if(!(m instanceof " + AbstractAllyMonster.class.getName() + ")) {" +
                                "$_ = $proceed($$);" +
                                "}" +
                                "}");
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz = MonsterGroup.class,
            method = "getRandomMonster",
            paramtypez = {
                    AbstractMonster.class,
                    boolean.class
            }
    )
    public static class Random2Please {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("add")) {
                        m.replace("{" +
                                "if(!(m instanceof " + AbstractAllyMonster.class.getName() + ")) {" +
                                "$_ = $proceed($$);" +
                                "}" +
                                "}");
                    }
                }
            };
        }
    }

}