package ruina.monsters.uninvitedGuests.normal.argalia.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRALLY_MACE extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_MACE.class.getSimpleName());

    public CHRALLY_MACE(Roland parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.MACE_DAMAGE;
        magicNumber = baseMagicNumber = parent.MACE_HITS;
    }

    @Override
    public float getTitleFontSize()
    {
        return 18;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}