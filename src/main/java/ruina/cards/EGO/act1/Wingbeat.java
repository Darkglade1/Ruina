package ruina.cards.EGO.act1;

import com.evacipated.cardcrawl.mod.stslib.variables.ExhaustiveVariable;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class Wingbeat extends AbstractEgoCard {
    public final static String ID = makeID(Wingbeat.class.getSimpleName());

    public static final int DEX = 2;
    public static final int UP_DEX = 1;
    public static final int DISCARD = 2;
    public static final int EXHAUSTIVE = 3;

    public Wingbeat() {
        super(ID, 1, CardType.SKILL, CardTarget.SELF);
        magicNumber = baseMagicNumber = DEX;
        secondMagicNumber = baseSecondMagicNumber = DISCARD;
        ExhaustiveVariable.setBaseValue(this, EXHAUSTIVE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new DiscardAction(p, p, secondMagicNumber, false));
        applyToTarget(p, p, new DexterityPower(p, magicNumber));
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_DEX);
    }
}