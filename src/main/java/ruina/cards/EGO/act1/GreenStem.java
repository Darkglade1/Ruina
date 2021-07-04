package ruina.cards.EGO.act1;

import com.evacipated.cardcrawl.mod.stslib.variables.ExhaustiveVariable;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class GreenStem extends AbstractEgoCard {
    public final static String ID = makeID(GreenStem.class.getSimpleName());

    public static final int POISON = 6;
    public static final int UP_POISON = 2;
    public static final int EXHAUSTIVE = 2;

    public GreenStem() {
        super(ID, 1, CardType.SKILL, CardTarget.ENEMY);
        magicNumber = baseMagicNumber = POISON;
        ExhaustiveVariable.setBaseValue(this, EXHAUSTIVE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applyToTarget(m, p, new PoisonPower(m, p, magicNumber));
    }

    @Override
    public void triggerOnExhaust() {
        for (AbstractMonster mo : monsterList()) {
            AbstractPower poison = mo.getPower(PoisonPower.POWER_ID);
            if (poison != null) {
                applyToTarget(mo, adp(), new PoisonPower(mo, adp(), poison.amount));
            }
        }
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_POISON);
    }
}