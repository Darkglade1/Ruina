package ruina.monsters.uninvitedGuests.normal.philip.philipCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.extra.philip.monster.PhilipEX;
import ruina.monsters.uninvitedGuests.normal.philip.Philip;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Sorrow extends AbstractRuinaCard {
    public final static String ID = makeID(Sorrow.class.getSimpleName());
    Philip parent;
    PhilipEX parentEX;

    public Sorrow(Philip parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.RARE, CardTarget.SELF, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.sorrowHits;
        this.parent = parent;
    }

    public Sorrow(PhilipEX parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.RARE, CardTarget.SELF, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.sorrowHits;
        this.parentEX = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Sorrow(parent);
    }
}