package ruina.monsters.uninvitedGuests.eileen.eileenCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.eileen.Eileen;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Preach extends AbstractRuinaCard {
    public final static String ID = makeID(Preach.class.getSimpleName());
    Eileen parent;

    public Preach(Eileen parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.STRENGTH;
        this.parent = parent;
    }

    @Override
    public float getTitleFontSize()
    {
        return 14;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Preach(parent);
    }
}