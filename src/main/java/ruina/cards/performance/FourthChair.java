package ruina.cards.performance;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

@AutoAdd.Ignore
public class FourthChair extends AbstractPerformanceCard {
    public final static String ID = makeID(FourthChair.class.getSimpleName());
    private static final int COST = 1;
    private static final int DEBUFF = 1;
    private static final int UP_DEBUFF = 1;
    private boolean triggerFirstEffect = true;

    public FourthChair() {
        super(ID, COST);
        magicNumber = baseMagicNumber = DEBUFF;
        selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void onRetained() {
        flash();
        if (triggerFirstEffect) {
            applyToTarget(adp(), adp(), new StrengthPower(adp(), -magicNumber));
        } else {
            applyToTarget(adp(), adp(), new DexterityPower(adp(), -magicNumber));
        }
        triggerFirstEffect = !triggerFirstEffect;
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_DEBUFF);
    }
}