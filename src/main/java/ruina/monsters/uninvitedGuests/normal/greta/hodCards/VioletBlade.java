package ruina.monsters.uninvitedGuests.normal.greta.hodCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.greta.Hod;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class VioletBlade extends AbstractRuinaCard {
    public final static String ID = makeID(VioletBlade.class.getSimpleName());

    public VioletBlade(Hod parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.violetHits;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}