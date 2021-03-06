package ruina.monsters.uninvitedGuests.normal.elena.elenaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.elena.Elena;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Nails extends AbstractRuinaCard {
    public final static String ID = makeID(Nails.class.getSimpleName());
    Elena parent;

    public Nails(Elena parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.sanguineNailsHits;
        this.parent = parent;
    }

    @Override
    public float getTitleFontSize()
    {
        return 20;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Nails(parent);
    }
}