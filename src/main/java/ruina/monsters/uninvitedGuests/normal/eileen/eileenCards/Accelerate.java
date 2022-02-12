package ruina.monsters.uninvitedGuests.normal.eileen.eileenCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.eileen.Eileen;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Accelerate extends AbstractRuinaCard {
    public final static String ID = makeID(Accelerate.class.getSimpleName());
    Eileen parent;

    public Accelerate(Eileen parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        baseBlock = parent.BLOCK;
        this.parent = parent;
        this.magicNumber = baseMagicNumber = parent.VULNERABLE;
    }

    @Override
    public float getTitleFontSize()
    {
        return 14;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { uDesc();}

    @Override
    public AbstractCard makeCopy() {
        return new Accelerate(parent);
    }
}