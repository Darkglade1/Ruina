package ruina.monsters.uninvitedGuests.normal.clown.tiphCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.clown.Tiph;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Confrontation extends AbstractRuinaCard {
    public final static String ID = makeID(Confrontation.class.getSimpleName());

    public Confrontation(Tiph parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        baseBlock = parent.BLOCK;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}