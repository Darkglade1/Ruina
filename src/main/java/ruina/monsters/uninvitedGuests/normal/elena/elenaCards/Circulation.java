package ruina.monsters.uninvitedGuests.normal.elena.elenaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.elena.Elena;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Circulation extends AbstractRuinaCard {
    public final static String ID = makeID(Circulation.class.getSimpleName());
    Elena parent;

    public Circulation(Elena parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.STRENGTH;
        secondMagicNumber = baseSecondMagicNumber = parent.PROTECTION;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void upp() {
    }

    @Override
    public AbstractCard makeCopy() {
        return new Circulation(parent);
    }
}