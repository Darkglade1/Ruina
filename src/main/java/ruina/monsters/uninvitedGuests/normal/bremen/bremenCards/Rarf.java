package ruina.monsters.uninvitedGuests.normal.bremen.bremenCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.bremen.Bremen;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Rarf extends AbstractRuinaCard {
    public final static String ID = makeID(Rarf.class.getSimpleName());
    Bremen parent;

    public Rarf(Bremen parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        baseBlock = parent.BLOCK;
        magicNumber = baseMagicNumber = parent.PARALYSIS;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Rarf(parent);
    }
}