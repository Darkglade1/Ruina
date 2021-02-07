package ruina.cards.cardvars;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;

public class SecondMagicNumber extends DynamicVariable {

    @Override
    public String key() {
        return makeID("SecondMagic");
    }

    @Override
    public boolean isModified(AbstractCard card) {
        if (card instanceof AbstractRuinaCard) {
            return ((AbstractRuinaCard) card).isSecondMagicModified;
        }
        return false;
    }

    @Override
    public int value(AbstractCard card) {
        if (card instanceof AbstractRuinaCard) {
            return ((AbstractRuinaCard) card).secondMagicNumber;
        }
        return -1;
    }

    public void setIsModified(AbstractCard card, boolean v) {
        if (card instanceof AbstractRuinaCard) {
            ((AbstractRuinaCard) card).isSecondMagicModified = v;
        }
    }

    @Override
    public int baseValue(AbstractCard card) {
        if (card instanceof AbstractRuinaCard) {
            return ((AbstractRuinaCard) card).baseSecondMagicNumber;
        }
        return -1;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        if (card instanceof AbstractRuinaCard) {
            return ((AbstractRuinaCard) card).upgradedSecondMagic;
        }
        return false;
    }
}