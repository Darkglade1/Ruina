package ruina.monsters.uninvitedGuests.normal.elena.elenaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.elena.Elena;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Bloodspreading extends AbstractRuinaCard {
    public final static String ID = makeID(Bloodspreading.class.getSimpleName());
    Elena parent;

    public Bloodspreading(Elena parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.parent = parent;
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
        return new Bloodspreading(parent);
    }
}