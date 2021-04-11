package ruina.monsters.uninvitedGuests.normal.clown.oswaldCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.clown.Oswald;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Pow extends AbstractRuinaCard {
    public final static String ID = makeID(Pow.class.getSimpleName());
    Oswald parent;

    public Pow(Oswald parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.WEAK;
        secondMagicNumber = baseSecondMagicNumber = parent.STRENGTH;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Pow(parent);
    }
}