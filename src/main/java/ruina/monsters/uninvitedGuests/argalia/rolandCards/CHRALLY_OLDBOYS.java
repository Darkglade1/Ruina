package ruina.monsters.uninvitedGuests.argalia.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.argalia.monster.Roland;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRALLY_OLDBOYS extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_OLDBOYS.class.getSimpleName());

    public CHRALLY_OLDBOYS(Roland parent) {
        super(ID, 1, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.OLD_BOY_DAMAGE;
        block = baseBlock = parent.OLD_BOY_BLOCK;
    }

    @Override
    public float getTitleFontSize()
    {
        return 16;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}