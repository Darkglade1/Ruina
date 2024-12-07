package ruina.tutorials;

import basemod.abstracts.CustomMultiPageFtue;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyMonster;
import ruina.util.TexLoader;

import java.io.IOException;

import static ruina.RuinaMod.makeUIPath;

public class AllyTutorial extends CustomMultiPageFtue {

    public AllyTutorial(Texture[] images, String[] messages) {
        super(images, messages);
    }

    @SpirePatch(
            clz = DrawCardAction.class,
            method = "update"
    )
    public static class ShowAllyTutorialPatch {
        public static final String ID = RuinaMod.makeID("AllyTutorial");
        public static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(ID);
        public static final String[] TEXT = tutorialStrings.TEXT;

        public static void Postfix(DrawCardAction __instance) {
            if (__instance.isDone && !RuinaMod.ruinaConfig.getBool("Ally Tutorial Seen")) {
                for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                    if (mo instanceof AbstractAllyMonster) {
                        Texture tip1 = TexLoader.getTexture(makeUIPath("AllyTutorial.png"));
                        Texture[] images = new Texture[1];
                        images[0] = tip1;
                        AbstractDungeon.ftue = new AllyTutorial(images, TEXT);
                        RuinaMod.ruinaConfig.setBool("Ally Tutorial Seen", true);
                        try { RuinaMod.ruinaConfig.save(); } catch (IOException e) { e.printStackTrace(); }
                        break;
                    }
                }
            }
        }
    }
}