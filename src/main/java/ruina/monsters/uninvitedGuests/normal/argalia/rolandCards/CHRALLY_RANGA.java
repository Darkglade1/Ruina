package ruina.monsters.uninvitedGuests.normal.argalia.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRALLY_RANGA extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_RANGA.class.getSimpleName());

    public CHRALLY_RANGA(Roland parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.RANGA_DAMAGE;
        magicNumber = baseMagicNumber = parent.RANGA_HITS;
        secondMagicNumber = baseSecondMagicNumber = parent.RANGA_DEBUFF;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}