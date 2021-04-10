package ruina.monsters.uninvitedGuests.philip.philipCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.philip.Philip;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Stigmatize extends AbstractRuinaCard {
    public final static String ID = makeID(Stigmatize.class.getSimpleName());
    Philip parent;

    public Stigmatize(Philip parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.stigmatizeHits;
        this.parent = parent;
    }

    @Override
    public float getTitleFontSize()
    {
        return 14;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Stigmatize(parent);
    }
}