package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.CommonKeywordIconsField;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import ruina.RuinaMod;
import ruina.util.HelperClass;

public class ExhaustMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID("ExhaustMod");
    private boolean alreadyExhaust = false;

    @Override
    public AbstractCardModifier makeCopy() {
        return new ExhaustMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.exhaust) {
            card.exhaust = true;
            alreadyExhaust = false;
        } else {
            alreadyExhaust = true;
        }
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (!alreadyExhaust && !(CommonKeywordIconsField.useIcons.get(card))) {
            return rawDescription + " NL " + HelperClass.capitalize(GameDictionary.EXHAUST.NAMES[0]) + LocalizedStrings.PERIOD;
        }
        return rawDescription;
    }
}
