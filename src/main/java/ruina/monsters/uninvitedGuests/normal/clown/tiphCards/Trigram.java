package ruina.monsters.uninvitedGuests.normal.clown.tiphCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.clown.Tiph;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Trigram extends AbstractRuinaCard {
    public final static String ID = makeID(Trigram.class.getSimpleName());

    public Trigram(Tiph parent) {
        super(ID, 4, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        baseMagicNumber = magicNumber = parent.trigramHits;
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
}