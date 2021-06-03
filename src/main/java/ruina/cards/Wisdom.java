package ruina.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class Wisdom extends AbstractRuinaCard {
    public final static String ID = makeID(Wisdom.class.getSimpleName());
    private static final int DRAW = 1;

    public Wisdom() {
        super(ID, 1, CardType.STATUS, CardRarity.SPECIAL, CardTarget.NONE);
        magicNumber = baseMagicNumber = DRAW;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new DrawCardAction(p, magicNumber));
    }

    public void upp() {

    }
}