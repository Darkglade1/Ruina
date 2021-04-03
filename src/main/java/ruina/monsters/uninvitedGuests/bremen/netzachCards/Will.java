package ruina.monsters.uninvitedGuests.bremen.netzachCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.bremen.Netzach;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Will extends AbstractRuinaCard {
    public final static String ID = makeID(Will.class.getSimpleName());

    public Will(Netzach parent) {
        super(ID, 2, CardType.SKILL, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        baseBlock = parent.BLOCK;
        magicNumber = baseMagicNumber = parent.DRAW;
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
}