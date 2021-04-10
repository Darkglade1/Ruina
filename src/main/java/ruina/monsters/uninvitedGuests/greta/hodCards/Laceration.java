package ruina.monsters.uninvitedGuests.greta.hodCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.greta.Hod;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Laceration extends AbstractRuinaCard {
    public final static String ID = makeID(Laceration.class.getSimpleName());

    public Laceration(Hod parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.VULNERABLE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}