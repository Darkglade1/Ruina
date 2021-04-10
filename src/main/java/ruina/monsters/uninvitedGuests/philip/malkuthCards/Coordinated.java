package ruina.monsters.uninvitedGuests.philip.malkuthCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.philip.Malkuth;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Coordinated extends AbstractRuinaCard {
    public final static String ID = makeID(Coordinated.class.getSimpleName());

    public Coordinated(Malkuth parent) {
        super(ID, 3, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.STRENGTH;
        baseBlock = parent.ALLY_BLOCK;
        baseSecondMagicNumber = secondMagicNumber = parent.DRAW;
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