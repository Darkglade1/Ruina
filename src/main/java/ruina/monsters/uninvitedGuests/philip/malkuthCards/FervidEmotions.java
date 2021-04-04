package ruina.monsters.uninvitedGuests.philip.malkuthCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.philip.Malkuth;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class FervidEmotions extends AbstractRuinaCard {
    public final static String ID = makeID(FervidEmotions.class.getSimpleName());

    public FervidEmotions(Malkuth parent) {
        super(ID, 4, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.fervidHits;
        secondMagicNumber = baseSecondMagicNumber = parent.fervidEmotions;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}