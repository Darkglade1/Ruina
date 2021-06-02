package ruina.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.block;

@AutoAdd.Ignore
public class Enlightenment extends AbstractRuinaCard {
    public final static String ID = makeID(Enlightenment.class.getSimpleName());
    private static final int DRAW = 1;
    private static final int HP_LOSS = 2;
    private static final int UP_HP_LOSS = 1;
    private static final int BLOCK = 5;

    public Enlightenment() {
        super(ID, 0, CardType.STATUS, CardRarity.SPECIAL, CardTarget.SELF);
        baseBlock = BLOCK;
        magicNumber = baseMagicNumber = HP_LOSS;
        secondMagicNumber = baseSecondMagicNumber = DRAW;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new LoseHPAction(p, p, magicNumber));
        block(p, block);
        atb(new DrawCardAction(p, secondMagicNumber));
    }

    public void upp() {
        upgradeMagicNumber(UP_HP_LOSS);
    }
}