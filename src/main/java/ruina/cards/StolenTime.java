package ruina.cards;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class StolenTime extends AbstractRuinaCard {
    public final static String ID = makeID(StolenTime.class.getSimpleName());
    private static final int DRAW = 1;

    public StolenTime() {
        super(ID, 0, CardType.STATUS, CardRarity.SPECIAL, CardTarget.NONE);
        magicNumber = baseMagicNumber = DRAW;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new DrawCardAction(p, magicNumber));
    }

    public void upp() {

    }
}