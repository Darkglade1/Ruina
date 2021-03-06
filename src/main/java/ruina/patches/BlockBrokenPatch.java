package ruina.patches;


import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.Instanceof;
import ruina.monsters.AbstractAllyMonster;

//make hand drill not trigger on allies
@SpirePatch(clz = AbstractCreature.class, method = "brokeBlock")
public class BlockBrokenPatch {
    public static ExprEditor Instrument() {
        return new ExprEditor() {
            @Override
            public void edit(Instanceof i) throws CannotCompileException {
                try {
                    if (i.getType().getName().equals(AbstractMonster.class.getName())) {
                        i.replace("$_ = $proceed($$) && " + BlockBrokenPatch.class.getName() + ".allyCondition(this);");
                    }
                } catch (NotFoundException e) { }
            }
        };
    }

    public static boolean allyCondition(AbstractCreature creature) {
        return !(creature instanceof AbstractAllyMonster && ((AbstractAllyMonster) creature).isAlly);
    }
}