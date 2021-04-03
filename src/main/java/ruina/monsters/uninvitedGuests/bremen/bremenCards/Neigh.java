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
public class Neigh extends AbstractRuinaCard {
    public final static String ID = makeID(Neigh.class.getSimpleName());
    Bremen parent;

    public Neigh(Bremen parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Neigh(parent);
    }
}