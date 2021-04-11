package ruina.monsters.uninvitedGuests.normal.greta.gretaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.greta.Greta;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Mince extends AbstractRuinaCard {
    public final static String ID = makeID(Mince.class.getSimpleName());
    Greta parent;

    public Mince(Greta parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.minceHits;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Mince(parent);
    }
}