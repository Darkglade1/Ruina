package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import ruina.RuinaMod;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class MetallicizeMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID(MetallicizeMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private final int amount;

    public MetallicizeMod(int amount) {
        this.amount = amount;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MetallicizeMod(amount);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        applyToTarget(adp(), adp(), new MetallicizePower(adp(), amount));
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + TEXT[0] + amount + TEXT[1];
    }
}
