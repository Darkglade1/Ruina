package ruina.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.actions.watcher.FearNoEvilAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.stances.CalmStance;

@SpirePatch(
        clz = FearNoEvilAction.class,
        method = "update"
)
public class FearNoEvilSucksPatch {
    @SpirePostfixPatch
    public static void Postfix(FearNoEvilAction __instance) {
        AbstractMonster m = (AbstractMonster) ReflectionHacks.getPrivate(__instance, FearNoEvilAction.class, "m");
        if(m != null && m.getIntentBaseDmg() >= 0) {
            AbstractDungeon.actionManager.addToTop(new ChangeStanceAction(CalmStance.STANCE_ID));
        }
    }
}