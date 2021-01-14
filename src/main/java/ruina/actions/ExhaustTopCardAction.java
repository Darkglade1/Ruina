package ruina.actions;

import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.Wisdom;
import ruina.monsters.act2.Scarecrow;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.att;

public class ExhaustTopCardAction extends AbstractGameAction {
    private AbstractCreature source;

    public ExhaustTopCardAction(AbstractCreature source) {
        this.actionType = ActionType.EXHAUST;
        this.duration = Settings.ACTION_DUR_FAST;
        this.source = source;
    }

    public void update() {
        if (adp().drawPile.isEmpty()) {
            isDone = true;
            return;
        }
        AbstractCard cardToExhaust = adp().drawPile.getTopCard();
        if (cardToExhaust instanceof Wisdom && source instanceof Scarecrow && !source.hasPower(StunMonsterPower.POWER_ID)) {
            att(new StunMonsterAction((AbstractMonster) source, source, 2));
        }
        if (cardToExhaust != null) {
            att(new ExhaustSpecificCardAction(cardToExhaust, adp().drawPile, true));
        }
        this.isDone = true;
    }
}


