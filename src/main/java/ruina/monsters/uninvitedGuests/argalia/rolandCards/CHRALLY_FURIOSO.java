package ruina.monsters.uninvitedGuests.argalia.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.argalia.monster.Roland;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRALLY_FURIOSO extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_FURIOSO.class.getSimpleName());

    public CHRALLY_FURIOSO(Roland parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.furiosoDamage;
        magicNumber = baseMagicNumber = parent.furiosoHits;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}