package ruina.monsters.blackSilence.blackSilence1.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence1.BlackSilence1;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_MACE;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Mace extends AbstractRuinaCard {
    public final static String ID = makeID(Mace.class.getSimpleName());

    public Mace(BlackSilence1 parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRALLY_MACE.class.getSimpleName() + ".png"));
        damage = baseDamage = parent.MACE_DAMAGE;
        magicNumber = baseMagicNumber = parent.MACE_HITS;
        secondMagicNumber = baseSecondMagicNumber = parent.MACE_STR;
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