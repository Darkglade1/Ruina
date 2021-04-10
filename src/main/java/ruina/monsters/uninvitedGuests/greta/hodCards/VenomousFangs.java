package ruina.monsters.uninvitedGuests.greta.hodCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.greta.Hod;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class VenomousFangs extends AbstractRuinaCard {
    public final static String ID = makeID(VenomousFangs.class.getSimpleName());

    public VenomousFangs(Hod parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.WEAK;
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
}