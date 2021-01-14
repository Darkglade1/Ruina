package ruina.cards;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class Wisdom extends AbstractRuinaCard {
    public final static String ID = makeID("Wisdom");
    private static final int DRAW = 1;

    public Wisdom() {
        super(ID, 1, CardType.STATUS, CardRarity.COMMON, CardTarget.NONE);
        magicNumber = baseMagicNumber = DRAW;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new DrawCardAction(p, magicNumber));
    }

    public void upp() {

    }
}