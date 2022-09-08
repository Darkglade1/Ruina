package ruina.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import ruina.RuinaMod;
import ruina.dungeons.AbstractRuinaDungeon;
import ruina.monsters.theHead.Baral;
import ruina.monsters.theHead.Zena;
import ruina.powers.PlayerBackAttack;

import java.util.ArrayList;

import static ruina.util.Wiz.adp;

@SpirePatch(
        clz = AbstractCreature.class,
        method = "renderPowerIcons"
)
public class MultiLinePowersPatches {
    private static final int INITIAL_LIMIT = 4;
    private static float offsetY = 0;
    private static int count = 0;
    private static boolean doingAmounts = false;

    public static boolean isNotInvis(AbstractPower p) {
        return !(p instanceof InvisiblePower);
    }

    @SuppressWarnings("unused")
    public static float getOffsetY() {
        return offsetY;
    }

    @SuppressWarnings("unused")
    public static void incrementOffsetY(float[] offsetX, AbstractPower p) {
        if(isNotInvis(p)) {
            ++count;
            if (count == INITIAL_LIMIT) {
                count = 0;
                offsetY -= 38 * Settings.scale;
                offsetX[0] = ((doingAmounts ? 0 : 10) - 48) * Settings.scale;
            }
        }
    }

    @SpirePrefixPatch
    public static void Prefix(AbstractCreature __instance, SpriteBatch sb, float x, float y) {
        offsetY = 0;
        count = 0;
        doingAmounts = false;
    }

    @SpireInsertPatch(locator = Locator.class)
    public static void Insert(AbstractCreature __instance, SpriteBatch sb, float x, float y) {
        offsetY = 0;
        count = 0;
        doingAmounts = true;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCreature.class, "powers");
            return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[1]};
        }
    }

    public static ExprEditor Instrument() {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getMethodName().equals("renderIcons")) {
                    m.replace("{" +
                            "if("+MultiLinePowersPatches.class.getName()+".shouldApply($0.owner))" +
                            "{" +
                                 "$3 += " + MultiLinePowersPatches.class.getName() + ".getOffsetY();" +
                                 "$proceed($$);" +
                                 "float[] offsetArr = new float[]{offset};" +
                                 MultiLinePowersPatches.class.getName() + ".incrementOffsetY(offsetArr, p);" +
                                 "offset = offsetArr[0];" +
                            "}" +
                            "else" +
                            "{" +
                                "$proceed($$);" +
                            "}" +
                            "}");
                } else if (m.getMethodName().equals("renderAmount")) {
                    m.replace("{" +
                            "if("+MultiLinePowersPatches.class.getName()+".shouldApply($0.owner))" +
                            "{" +
                                "$3 += " + MultiLinePowersPatches.class.getName() + ".getOffsetY();" +
                                "$proceed($$);" +
                                "float[] offsetArr = new float[]{offset};" +
                                MultiLinePowersPatches.class.getName() + ".incrementOffsetY(offsetArr, p);" +
                                "offset = offsetArr[0];" +
                            "}" +
                            "else" +
                            "{" +
                                "$proceed($$);" +
                            "}" +
                            "}");
                }
            }
        };
    }

    public static boolean shouldApply(AbstractCreature c) {
        return c instanceof Baral || c instanceof Zena;
    }

    @SpirePatch(cls = "mintySpire.patches.powers.MultiLinePowersPatches", method = "isExcludedClass")
    public static class PreventConflict {
        @SpirePostfixPatch()
        public static boolean yeppers (boolean original, AbstractCreature  c) {
            if (c instanceof Baral || c instanceof Zena) {
                return true;
            } else {
                return original;
            }
        }
    }
}