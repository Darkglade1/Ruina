package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import ruina.RuinaMod;
import ruina.util.HelperClass;

public class UnplayableMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID("UnplayableMod");

    @Override
    public AbstractCardModifier makeCopy() {
        return new UnplayableMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        return false;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return HelperClass.capitalize(GameDictionary.UNPLAYABLE.NAMES[0]) + LocalizedStrings.PERIOD + " NL " + rawDescription;
    }
}
