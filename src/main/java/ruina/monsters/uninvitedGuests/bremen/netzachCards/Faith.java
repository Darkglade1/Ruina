package ruina.monsters.uninvitedGuests.bremen.netzachCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.bremen.Netzach;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Faith extends AbstractRuinaCard {
    public final static String ID = makeID(Faith.class.getSimpleName());

    public Faith(Netzach parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.faithHits;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}