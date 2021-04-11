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
public class BreakEgg extends AbstractRuinaCard {
    public final static String ID = makeID(BreakEgg.class.getSimpleName());
    Greta parent;

    public BreakEgg(Greta parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.PARALYSIS;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new BreakEgg(parent);
    }
}