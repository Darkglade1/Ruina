package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.CommonKeywordIconsField;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import ruina.RuinaMod;

public class EtherealMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID("EtherealMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

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
            return TEXT[0] + rawDescription;
        }
        return rawDescription;
    }
}
