package ruina.cards.cardvars;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;

public class SillyVariable extends DynamicVariable {

    @Override
    public String key() {
        return makeID("si");
    }

    @Override
    public boolean isModified(AbstractCard card) {
        if (card instanceof AbstractRuinaCard) {
            return ((AbstractRuinaCard) card).isSillyModified;
        }
        return false;
    }

    @Override
    public int value(AbstractCard card) {
        if (card instanceof AbstractRuinaCard) {
            return ((AbstractRuinaCard) card).silly;
        }
        return -1;
    }

    public void setIsModified(AbstractCard card, boolean v) {
        if (card instanceof AbstractRuinaCard) {
            ((AbstractRuinaCard) card).isSillyModified = v;
        }
    }

    @Override
    public int baseValue(AbstractCard card) {
        if (card instanceof AbstractRuinaCard) {
            return ((AbstractRuinaCard) card).baseSilly;
        }
        return -1;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        if (card instanceof AbstractRuinaCard) {
            return ((AbstractRuinaCard) card).upgradedSilly;
        }
        return false;
    }
}