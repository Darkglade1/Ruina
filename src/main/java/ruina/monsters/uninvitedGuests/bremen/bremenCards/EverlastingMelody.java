package ruina.monsters.uninvitedGuests.bremen.bremenCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.bremen.Bremen;
import ruina.monsters.uninvitedGuests.elena.Elena;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class EverlastingMelody extends AbstractRuinaCard {
    public final static String ID = makeID(EverlastingMelody.class.getSimpleName());
    Bremen parent;

    public EverlastingMelody(Bremen parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.STRENGTH;
        this.parent = parent;
    }

    @Override
    public float getTitleFontSize()
    {
        return 16;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new EverlastingMelody(parent);
    }
}