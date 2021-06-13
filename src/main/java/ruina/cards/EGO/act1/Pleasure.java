package ruina.cards.EGO.act1;

import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.AutoplayField;
import com.evacipated.cardcrawl.mod.stslib.variables.ExhaustiveVariable;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ThornsPower;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class Pleasure extends AbstractEgoCard {
    public final static String ID = makeID(Pleasure.class.getSimpleName());

    public static final int THORNS = 3;
    public static final int UP_THORNS = 1;
    public static final int ENERGY = 1;
    public static final int SELF_DAMAGE = 8;
    public static final int UP_SELF_DAMAGE = -2;
    public static final int EXHAUSTIVE = 3;

    public Pleasure() {
        super(ID, 0, CardType.SKILL, CardTarget.SELF);
        magicNumber = baseMagicNumber = THORNS;
        secondMagicNumber = baseSecondMagicNumber = SELF_DAMAGE;
        AutoplayField.autoplay.set(this, true);
        ExhaustiveVariable.setBaseValue(this, EXHAUSTIVE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new GainEnergyAction(ENERGY));
        applyToTarget(p, p, new ThornsPower(p, magicNumber));
    }

    @Override
    public void triggerOnExhaust() {
        atb(new LoseHPAction(adp(), adp(), secondMagicNumber));
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_THORNS);
        upgradeSecondMagic(UP_SELF_DAMAGE);
    }
}