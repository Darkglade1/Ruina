package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.interfaces.AlternateCardCostModifier;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import ruina.RuinaMod;
import ruina.cards.ManifestCallbackInterface;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class ManifestMod extends AbstractCardModifier implements AlternateCardCostModifier {

    public static final String ID = RuinaMod.makeID("ManifestMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public AbstractCardModifier makeCopy() {
        return new ManifestMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public boolean isInherent(AbstractCard card) {
        return true;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return TEXT[0] + rawDescription;
    }

    @Override
    public int getAlternateResource(AbstractCard card) {
        if (adp().hand.contains(card)) {
            return adp().hand.size() - 1;
        } else {
            return adp().hand.size();
        }
    }

    @Override
    public boolean canSplitCost(AbstractCard card) {
        return true;
    }

    @Override
    public int spendAlternateCost(AbstractCard card, int costToSpend) {
        int resource = adp().hand.size();
        int numCardsManifested = 0;
        if (resource > costToSpend) {
            atb(new ExhaustAction(costToSpend, false));
            numCardsManifested = costToSpend;
            costToSpend = 0;
        } else if (resource > 0) {
            atb(new ExhaustAction(resource, false));
            numCardsManifested = resource;
            costToSpend -= resource;
        }
        if (numCardsManifested > 0 && card instanceof ManifestCallbackInterface) {
            ((ManifestCallbackInterface) card).numCardsUsedToManifest(numCardsManifested);
        }
        return costToSpend;
    }
}
