package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import ruina.RuinaMod;
import ruina.powers.EnergyLossNextTurn;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class EnergyLossMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID(EnergyLossMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private final int amount;

    public EnergyLossMod(int amount) {
        this.amount = amount;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new EnergyLossMod(amount);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        applyToTarget(adp(), adp(), new EnergyLossNextTurn(adp(), amount));
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        StringBuilder result = new StringBuilder(rawDescription + TEXT[0]);
        for (int i = 0; i < amount; i++) {
            result.append(TEXT[1]);
        }
        result.append(TEXT[2]);
        return result.toString();
    }
}
