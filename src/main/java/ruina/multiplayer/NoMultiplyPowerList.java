package ruina.multiplayer;

import basemod.AutoAdd;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtClass;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;
import spireTogether.subscribers.TiSPowerSyncRulesSubscriber;

import java.util.ArrayList;
import java.util.Collection;

public class NoMultiplyPowerList implements TiSPowerSyncRulesSubscriber {

    @Override
    public ArrayList<Class<? extends AbstractPower>> noMultiplyPowers() {
        ArrayList<Class<? extends AbstractPower>> powerClassList = new ArrayList<>();
        AutoAdd autoAdd = new AutoAdd(RuinaMod.getModID())
                .packageFilter(RuinaMod.class);

        Class<?> type = AbstractEasyPower.class;
        Collection<CtClass> foundClasses = autoAdd.findClasses(type);

        for (CtClass ctClass : foundClasses) {
            boolean ignore = ctClass.hasAnnotation(AutoAdd.Ignore.class);
            if (!ignore) {
                try {
                    Class<? extends AbstractEasyPower> powerClass = (Class<? extends AbstractEasyPower>) Loader.getClassPool().getClassLoader().loadClass(ctClass.getName());
                    powerClassList.add(powerClass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    @Override
    public ArrayList<Class<? extends AbstractPower>> noSyncPowers() {
        return null;
    }
}
