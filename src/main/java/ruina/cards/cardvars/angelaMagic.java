package ruina.cards.cardvars;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import ruina.cards.abs_angela_card;

import static ruina.RuinaMod.makeID;


public class angelaMagic extends DynamicVariable {

    @Override
    public String key() { return makeID("M"); }
    //TODO: Change this, if you want. It's already modID prefixed, so no worries about conflicts (ASSUMING YOU CHANGED YOUR MODID!)

    @Override
    public boolean isModified(AbstractCard card) {
        if (card instanceof abs_angela_card) {
            return ((abs_angela_card) card).isAngelaSecondMagicNumberModified;
        }
        return false;
    }

    @Override
    public int value(AbstractCard card) {
        if (card instanceof abs_angela_card) {
            return ((abs_angela_card) card).angelaSecondMagicNumber;
        }
        return -1;
    }

    public void setIsModified(AbstractCard card, boolean v) {
        if (card instanceof abs_angela_card) {
            ((abs_angela_card) card).isAngelaSecondMagicNumberModified = v;
        }
    }

    @Override
    public int baseValue(AbstractCard card) {
        if (card instanceof abs_angela_card) {
            return ((abs_angela_card) card).angelaBaseSecondMagicNumber;
        }
        return -1;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        if (card instanceof abs_angela_card) {
            return ((abs_angela_card) card).upgradedAngelaSecondMagicNumber;
        }
        return false;
    }
}