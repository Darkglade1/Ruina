package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.powers.act3.Aspiration;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.att;

public class AspirationAction extends AbstractGameAction {

    public AspirationAction(int initialHP) {
        this.duration = 0.0F;
        this.amount = initialHP;
        this.actionType = ActionType.DAMAGE; //mark action as damage so end of combat doesn't yeet it
    }

    @Override
    public void update() {
        int afterwardHP = AbstractDungeon.player.currentHealth;
        int difference = amount - afterwardHP;

        if (difference > 0) {
            //if somehow combat is ending at this point, just give the player back their HP
            if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                att(new HealAction(adp(), adp(), difference));
            } else {
                att(new ApplyPowerAction(adp(), adp(), new Aspiration(adp(), difference)));
            }
        }

        this.isDone = true;
    }
}
