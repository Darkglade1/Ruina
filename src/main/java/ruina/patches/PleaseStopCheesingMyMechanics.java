package ruina.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.DuplicationPotion;
import com.megacrit.cardcrawl.random.Random;
import ruina.dungeons.AbstractRuinaDungeon;
import ruina.monsters.theHead.Baral;
import ruina.potions.EgoPotion;
import ruina.powers.PlayerBlackSilence;

import java.util.Iterator;

public class PleaseStopCheesingMyMechanics {
    @SpirePatch2(
            cls = "potionbundle.potions.PickleJuiceUncommon",
            method="use",
            paramtypez = AbstractCreature.class,
            optional = true
    )
    public static class day1patch {
        public static SpireReturn Prefix(AbstractPotion __instance, AbstractCreature target) {
            if (target instanceof Baral) {
                return SpireReturn.Return(null);
            } else {
                return SpireReturn.Continue();
            }
        }
    }
}
