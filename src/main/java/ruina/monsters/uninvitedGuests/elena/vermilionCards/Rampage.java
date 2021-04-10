package ruina.monsters.uninvitedGuests.elena.vermilionCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.elena.VermilionCross;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Rampage extends AbstractRuinaCard {
    public final static String ID = makeID(Rampage.class.getSimpleName());
    VermilionCross parent;

    public Rampage(VermilionCross parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.parent = parent;
    }

    @Override
    public float getTitleFontSize()
    {
        return 18;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Rampage(parent);
    }
}