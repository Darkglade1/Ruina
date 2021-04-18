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
public class Eventide extends AbstractRuinaCard {
    public final static String ID = makeID(Eventide.class.getSimpleName());
    Philip parent;
    PhilipEX parentEX;

    public Eventide(Philip parent) {
        super(ID, 0, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.EVENTIDE_BURNS;
        this.parent = parent;
    }

    public Eventide(PhilipEX parent) {
        super(ID, 0, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.EVENTIDE_BURNS;
        this.parentEX = parent;
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

    @Override
    public AbstractCard makeCopy() {
        return new Eventide(parent);
    }
}