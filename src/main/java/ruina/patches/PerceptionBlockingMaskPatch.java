package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import ruina.powers.PerceptionBlockingMask;

public class PerceptionBlockingMaskPatch {
    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class DelayUseCard {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getMethodName().equals("use")) {
                        m.replace("{" +
                                "if(!(" + PerceptionBlockingMaskPatch.class.getName() + ".NegateCardPlay(c, monster))) {" +
                                "$proceed($$);" +
                                "}" +
                                "}");
                    }
                }
            };
        }
    }

    public static boolean NegateCardPlay(AbstractCard c, AbstractMonster m) {
        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().monsters != null) {
            for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (monster.hasPower(PerceptionBlockingMask.POWER_ID)) {
                    PerceptionBlockingMask power = (PerceptionBlockingMask)monster.getPower(PerceptionBlockingMask.POWER_ID);
                    if (power.amount >= power.CARDS_PER_TURN - 1) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }
}