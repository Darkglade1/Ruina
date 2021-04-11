package ruina.monsters.uninvitedGuests.normal.pluto.plutoCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.pluto.monster.Pluto;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Contract extends AbstractRuinaCard {
    public final static String ID = makeID(Contract.class.getSimpleName());
    Pluto parent;

    public Contract(Pluto parent) {
        super(ID, 0, CardType.SKILL, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Contract(parent);
    }
}