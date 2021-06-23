package ruina.actions;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import ruina.cardmods.RetainMod;

public class MagicBulletAction extends AbstractGameAction {

    public MagicBulletAction() {
        this.duration = 0.0F;
        this.actionType = ActionType.WAIT;
    }

    @Override
    public void update() {
        for (AbstractCard card : DrawCardAction.drawnCards) {
            card.flash();
            CardModifierManager.addModifier(card, new RetainMod());
        }
        this.isDone = true;
    }
}
