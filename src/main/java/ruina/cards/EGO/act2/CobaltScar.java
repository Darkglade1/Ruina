package ruina.cards.EGO.act2;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.powers.CobaltScarPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class CobaltScar extends AbstractEgoCard {
    public final static String ID = makeID(CobaltScar.class.getSimpleName());

    public static final int DAMAGE = 20;
    public static final int THRESHOLD = 5;

    public CobaltScar() {
        super(ID, 1, CardType.POWER, CardTarget.SELF);
        magicNumber = baseMagicNumber = DAMAGE;
        secondMagicNumber = baseSecondMagicNumber = THRESHOLD;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new ApplyPowerAction(adp(), adp(), new CobaltScarPower(adp(), magicNumber, secondMagicNumber), magicNumber));
    }

    @Override
    public void upp() {
        isInnate = true;
    }
}