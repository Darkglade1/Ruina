package ruina.monsters.uninvitedGuests.normal.philip.malkuthCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.philip.Malkuth;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Inferno extends AbstractRuinaCard {
    public final static String ID = makeID(Inferno.class.getSimpleName());

    public Inferno(Malkuth parent) {
        super(ID, 5, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.infernoStrScaling;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}