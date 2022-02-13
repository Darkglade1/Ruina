package ruina.monsters.blackSilence.blackSilence1.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence1.BlackSilence1;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_FURIOSO;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Furioso extends AbstractRuinaCard {
    public final static String ID = makeID(Furioso.class.getSimpleName());

    public Furioso(BlackSilence1 parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRALLY_FURIOSO.class.getSimpleName() + ".png"));
        damage = baseDamage = parent.furiosoDamage;
        magicNumber = baseMagicNumber = parent.furiosoHits;
        secondMagicNumber = baseSecondMagicNumber = parent.furiosoDebuff;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { uDesc(); }
}