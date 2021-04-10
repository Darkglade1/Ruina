package ruina.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;

@AutoAdd.Ignore
public class PowerPrescript extends AbstractRuinaCard {
    public final static String ID = makeID(PowerPrescript.class.getSimpleName());

    public PowerPrescript() {
        super(ID, 0, CardType.POWER, CardRarity.SPECIAL, CardTarget.SELF);
        magicNumber = baseMagicNumber = 1;
        selfRetain = true;
        exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        applyToTarget(p, p, new StrengthPower(p, magicNumber));
    }

    public void upp() {
        upgradeMagicNumber(1);
    }
}