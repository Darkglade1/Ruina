package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.uninvitedGuests.normal.clown.Tiph;

import static ruina.util.Wiz.atb;


public class BrainwashPatch {
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "releaseCard"
    )
    public static class ToggleAllyTargetable {
        @SpirePostfixPatch()
        public static void toggle() {
            for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                if (mo instanceof Tiph && !mo.isDeadOrEscaped()) {
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            ((Tiph) mo).isTargetableByPlayer = false;
                            mo.halfDead = true;
                            this.isDone = true;
                        }
                    });
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "useCard",
            paramtypez = {
                    AbstractCard.class,
                    AbstractMonster.class,
                    int.class
            }
    )
    public static class ToggleAllyTargetable2 {
        @SpirePostfixPatch()
        public static void toggle(AbstractPlayer instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
            for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                if (mo instanceof Tiph && !mo.isDeadOrEscaped()) {
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            ((Tiph) mo).isTargetableByPlayer = false;
                            mo.halfDead = true;
                            this.isDone = true;
                        }
                    });
                }
            }
        }
    }


}