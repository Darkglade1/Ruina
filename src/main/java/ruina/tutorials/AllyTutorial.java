package ruina.tutorials;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.FtueTip;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyMonster;

import java.io.IOException;

public class AllyTutorial extends FtueTip {

    public AllyTutorial(String label, String text, float x, float y, FtueTip.TipType type) {
        super(label, text, x, y, type);
    }

    @SpirePatch(
            clz = DrawCardAction.class,
            method = "update"
    )
    public static class ShowAllyTutorialPatch {
        public static final String ID = RuinaMod.makeID("AllyTutorial");
        public static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(ID);
        public static final String[] LABEL = tutorialStrings.LABEL;
        public static final String[] TEXT = tutorialStrings.TEXT;

        public static void Postfix(DrawCardAction __instance) {
            if (__instance.isDone && !RuinaMod.ruinaConfig.getBool("Ally Tutorial Seen")) {
                for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                    if (mo instanceof AbstractAllyMonster) {
                        AbstractDungeon.ftue = new AllyTutorial(LABEL[0], TEXT[0], Settings.WIDTH / 2.0f - (500.0f * Settings.scale), Settings.HEIGHT / 2.0f, TipType.COMBAT);
                        RuinaMod.ruinaConfig.setBool("Ally Tutorial Seen", true);
                        try { RuinaMod.ruinaConfig.save(); } catch (IOException e) { e.printStackTrace(); }
                        break;
                    }
                }
            }
        }
    }
}