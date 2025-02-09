package ruina.multiplayer;

import basemod.AutoAdd;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import javassist.CtClass;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;
import ruina.powers.RuinaIntangible;
import ruina.powers.RuinaMetallicize;
import ruina.powers.RuinaPlatedArmor;
import ruina.powers.act4.FreshMeat;
import spireTogether.subscribers.TiSPowerSyncRulesSubscriber;

import java.util.ArrayList;
import java.util.Collection;

public class MultiplayerPowerSyncRules implements TiSPowerSyncRulesSubscriber {
    protected static ArrayList<Class<? extends AbstractPower>> noMultiplyPowerClassList = new ArrayList<>();
    protected static ArrayList<Class<? extends AbstractPower>> noSyncPowerClassList = new ArrayList<>();
    @Override
    public ArrayList<Class<? extends AbstractPower>> noMultiplyPowers() {
        return noMultiplyPowerClassList;
    }

    @Override
    public ArrayList<Class<? extends AbstractPower>> noSyncPowers() {
        return noSyncPowerClassList;
    }

    public static void initializePowersList() {
        AutoAdd autoAdd = new AutoAdd(RuinaMod.getModID())
                .packageFilter(RuinaMod.class);

        Class<?> type = AbstractEasyPower.class;
        Collection<CtClass> foundClasses = autoAdd.findClasses(type);

        for (CtClass ctClass : foundClasses) {
            boolean ignore = ctClass.hasAnnotation(AutoAdd.Ignore.class);
            if (!ignore) {
                try {
                    Class<? extends AbstractEasyPower> powerClass = (Class<? extends AbstractEasyPower>) Loader.getClassPool().getClassLoader().loadClass(ctClass.getName());
                    noMultiplyPowerClassList.add(powerClass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        noMultiplyPowerClassList.add(RuinaPlatedArmor.class);
        noMultiplyPowerClassList.add(RuinaMetallicize.class);
        noMultiplyPowerClassList.add(RuinaIntangible.class);
        noMultiplyPowerClassList.add(GainStrengthPower.class);
        noMultiplyPowerClassList.add(LoseStrengthPower.class);

        noSyncPowerClassList.add(FreshMeat.class);
    }
}
