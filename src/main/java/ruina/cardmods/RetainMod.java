package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.CommonKeywordIconsField;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import ruina.RuinaMod;
import ruina.util.HelperClass;

public class RetainMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID("RetainMod");
    private boolean alreadyRetain = false;

    @Override
    public AbstractCardModifier makeCopy() {
        return new RetainMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.selfRetain) {
            card.selfRetain = true;
            alreadyRetain = false;
        } else {
            alreadyRetain = true;
        }
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (!alreadyRetain && !(CommonKeywordIconsField.useIcons.get(card))) {
            return HelperClass.capitalize(GameDictionary.RETAIN.NAMES[0]) + LocalizedStrings.PERIOD + " NL " + rawDescription;
        }
        return rawDescription;
    }
}
