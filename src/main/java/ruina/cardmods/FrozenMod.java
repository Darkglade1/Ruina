package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import ruina.RuinaMod;

public class FrozenMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID("FrozenMod");

    @Override
    public AbstractCardModifier makeCopy() {
        return new FrozenMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        RetainMod mod1 = new RetainMod();
        UnplayableMod mod2 = new UnplayableMod();
        CardModifierManager.addModifier(card, mod1);
        CardModifierManager.addModifier(card, mod2);
    }

    @Override
    public void onRemove(AbstractCard card) {
        CardModifierManager.removeModifiersById(card, RetainMod.ID, false);
        CardModifierManager.removeModifiersById(card, UnplayableMod.ID, false);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
