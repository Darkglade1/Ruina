package ruina.monsters.uninvitedGuests.clown.oswaldCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.clown.Oswald;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Fun extends AbstractRuinaCard {
    public final static String ID = makeID(Fun.class.getSimpleName());
    Oswald parent;

    public Fun(Oswald parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.funHits;
        this.parent = parent;
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

    @Override
    public AbstractCard makeCopy() {
        return new Fun(parent);
    }
}