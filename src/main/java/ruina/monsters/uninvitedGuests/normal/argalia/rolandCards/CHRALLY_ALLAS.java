package ruina.monsters.uninvitedGuests.normal.argalia.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRALLY_ALLAS extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_ALLAS.class.getSimpleName());

    public CHRALLY_ALLAS(Roland parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.ALLAS_DAMAGE;
        magicNumber = baseMagicNumber = parent.ALLAS_DEBUFF;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}