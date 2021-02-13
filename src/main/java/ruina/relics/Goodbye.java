package ruina.relics;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

import static ruina.RuinaMod.makeID;

public class Goodbye extends AbstractEasyRelic {
    public static final String ID = makeID(Goodbye.class.getSimpleName());

    private static final int DAMAGE_MULTIPLIER = 2;
    private boolean used = false;

    public Goodbye() {
        super(ID, RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public void atPreBattle() {
        used = false;
    }

    @Override
    public float atDamageModify(float damage, AbstractCard c) {
        if (!used && c.type == AbstractCard.CardType.ATTACK) {
            return damage * DAMAGE_MULTIPLIER;
        } else {
            return damage;
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK && !used) {
            this.flash();
            used = true;
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
