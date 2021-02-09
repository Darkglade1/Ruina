package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.CommonKeywordIconsField;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import ruina.RuinaMod;
import ruina.util.HelperClass;

public class InnateMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID("InnateMod");
    private boolean alreadyInnate = false;

    @Override
    public AbstractCardModifier makeCopy() {
        return new InnateMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.isInnate) {
            card.isInnate = true;
            alreadyInnate = false;
        } else {
            alreadyInnate = true;
        }
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (!alreadyInnate && !(CommonKeywordIconsField.useIcons.get(card))) {
            return HelperClass.capitalize(GameDictionary.INNATE.NAMES[0]) + LocalizedStrings.PERIOD + " NL " + rawDescription;
        }
        return rawDescription;
    }
}
