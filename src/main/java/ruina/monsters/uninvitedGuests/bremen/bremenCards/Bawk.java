package ruina.monsters.uninvitedGuests.bremen.bremenCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.bremen.Bremen;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Bawk extends AbstractRuinaCard {
    public final static String ID = makeID(Bawk.class.getSimpleName());
    Bremen parent;

    public Bawk(Bremen parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.STATUS;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Bawk(parent);
    }
}