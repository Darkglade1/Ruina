package ruina.monsters.blackSilence.blackSilence1.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence1.BlackSilence1;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_RANGA;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Ranga extends AbstractRuinaCard {
    public final static String ID = makeID(Ranga.class.getSimpleName());

    public Ranga(BlackSilence1 parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRALLY_RANGA.class.getSimpleName() + ".png"));
        damage = baseDamage = parent.RANGA_DAMAGE;
        magicNumber = baseMagicNumber = parent.RANGA_HITS;
        secondMagicNumber = baseSecondMagicNumber = parent.RANGA_DEBUFF;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}