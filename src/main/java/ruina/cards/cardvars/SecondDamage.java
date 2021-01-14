package ruina.cards.cardvars;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;

public class SecondDamage extends DynamicVariable {

    @Override
    public String key() {
        return makeID("sd");
    }

    @Override
    public boolean isModified(AbstractCard card) {
        if (card instanceof AbstractRuinaCard) {
            return ((AbstractRuinaCard) card).isSecondDamageModified;
        }
        return false;
    }

    public void setIsModified(AbstractCard card, boolean v) {
        if (card instanceof AbstractRuinaCard) {
            ((AbstractRuinaCard) card).isSecondDamageModified = v;
        }
    }

    @Override
    public int value(AbstractCard card) {
        if (card instanceof AbstractRuinaCard) {
            return ((AbstractRuinaCard) card).secondDamage;
        }
        return -1;
    }

    @Override
    public int baseValue(AbstractCard card) {
        if (card instanceof AbstractRuinaCard) {
            return ((AbstractRuinaCard) card).baseSecondDamage;
        }
        return -1;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        if (card instanceof AbstractRuinaCard) {
            return ((AbstractRuinaCard) card).upgradedSecondDamage;
        }
        return false;
    }
}