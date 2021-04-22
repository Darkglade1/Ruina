package ruina.monsters.blackSilence.blackSilence1.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence1.BlackSilence1;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_Durandal;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Durandal extends AbstractRuinaCard {
    public final static String ID = makeID(Durandal.class.getSimpleName());

    public Durandal(BlackSilence1 parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRALLY_Durandal.class.getSimpleName() + ".png"));
        damage = baseDamage = parent.durandalDamage;
        magicNumber = baseMagicNumber = parent.durandalHits;
        secondMagicNumber = baseSecondMagicNumber = parent.durandalStrength;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}