package ruina.monsters.uninvitedGuests.normal.eileen.eileenCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.eileen.Eileen;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Propagate extends AbstractRuinaCard {
    public final static String ID = makeID(Propagate.class.getSimpleName());
    Eileen parent;

    public Propagate(Eileen parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.RARE, CardTarget.SELF, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.VULNERABLE;
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
        return new Propagate(parent);
    }
}