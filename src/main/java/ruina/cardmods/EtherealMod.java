package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.CommonKeywordIconsField;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import ruina.RuinaMod;
import ruina.util.HelperClass;

public class EtherealMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID("EtherealMod");
    private boolean alreadyEthereal = false;

    @Override
    public AbstractCardModifier makeCopy() {
        return new EtherealMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.isEthereal) {
            card.isEthereal = true;
            alreadyEthereal = false;
        } else {
            alreadyEthereal = true;
        }
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (!alreadyEthereal && !(CommonKeywordIconsField.useIcons.get(card))) {
            return HelperClass.capitalize(GameDictionary.ETHEREAL.NAMES[0]) + LocalizedStrings.PERIOD + " NL " + rawDescription;
        }
        return rawDescription;
    }
}
