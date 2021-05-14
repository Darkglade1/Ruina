package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;

import java.util.Iterator;
import java.util.UUID;


public class IncreaseCostAction extends AbstractGameAction {
    UUID uuid;
    private AbstractCard card = null;

    public IncreaseCostAction(AbstractCard card, int amount) {
        this.card = card;
        this.amount = amount;
    }

    public IncreaseCostAction(UUID targetUUID, int amount) {
        this.uuid = targetUUID;
        this.amount = amount;
        this.duration = Settings.ACTION_DUR_XFAST;
    }

    public void update() {
        if (this.card == null) {
            Iterator var1 = GetAllInBattleInstances.get(this.uuid).iterator();
            while(var1.hasNext()) {
                AbstractCard c = (AbstractCard)var1.next();
                c.modifyCostForCombat(amount);
            }
        } else { this.card.modifyCostForCombat(amount); }
        this.isDone = true;
    }
}