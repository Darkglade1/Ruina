package theTodo.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.GainGoldTextEffect;
import theTodo.effects.LoseGoldTextEffect;

public class ChangeGoldAction extends AbstractGameAction {
    public ChangeGoldAction(int amount) {
        this.amount = amount;
        this.actionType = ActionType.SPECIAL;
    }

    @Override
    public void update() {
        if (amount > 0) {
            AbstractDungeon.player.gainGold(amount);
            AbstractDungeon.effectList.add(new GainGoldTextEffect(amount));
        } else if (amount < 0) {
            AbstractDungeon.player.loseGold(-amount);
            AbstractDungeon.effectList.add(new LoseGoldTextEffect(-amount));
        }
        this.isDone = true;
    }
}