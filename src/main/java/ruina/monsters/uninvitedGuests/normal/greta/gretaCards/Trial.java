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
public class Trial extends AbstractRuinaCard {
    public final static String ID = makeID(Trial.class.getSimpleName());
    Greta parent;

    public Trial(Greta parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Trial(parent);
    }
}