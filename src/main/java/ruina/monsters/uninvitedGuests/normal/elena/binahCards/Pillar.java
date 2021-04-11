package ruina.monsters.uninvitedGuests.normal.elena.binahCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.elena.Binah;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Pillar extends AbstractRuinaCard {
    public final static String ID = makeID(Pillar.class.getSimpleName());

    public Pillar(Binah parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        block = baseBlock = parent.BLOCK;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}