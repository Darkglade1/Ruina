package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import ruina.RuinaMod;

public class DamageUpMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID("DamageUpMod");

    private final int damageIncrease;

    public DamageUpMod(int amount) {
        this.damageIncrease = amount;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new DamageUpMod(damageIncrease);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseDamage >= 0) {
            card.baseDamage += damageIncrease;
        }
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
