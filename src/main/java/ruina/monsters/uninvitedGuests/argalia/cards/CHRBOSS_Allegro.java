package ruina.monsters.uninvitedGuests.argalia.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.redMist.monster.RedMist;
import ruina.monsters.uninvitedGuests.argalia.monster.Argalia;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_Allegro extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_Allegro.class.getSimpleName());

    public CHRBOSS_Allegro(Argalia parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.allegroDamage;
        magicNumber = baseMagicNumber = parent.allegroHits;
        secondMagicNumber = baseSecondMagicNumber = parent.allegroVibration;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}