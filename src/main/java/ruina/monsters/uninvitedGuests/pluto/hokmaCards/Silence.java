package ruina.monsters.uninvitedGuests.pluto.hokmaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.pluto.monster.Hokma;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Silence extends AbstractRuinaCard {
    public final static String ID = makeID(Silence.class.getSimpleName());

    public Silence(Hokma parent) {
        super(ID, 1, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.DAMAGE_INCREASE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}