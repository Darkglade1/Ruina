package ruina.cards.performance;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

@AutoAdd.Ignore
public class ThirdChair extends AbstractPerformanceCard {
    public final static String ID = makeID(ThirdChair.class.getSimpleName());
    private static final int COST = 1;
    private static final int DEBUFF = 1;
    private static final int UP_DEBUFF = 1;
    private boolean triggerFirstEffect = true;

    public ThirdChair() {
        super(ID, COST);
        magicNumber = baseMagicNumber = DEBUFF;
        selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void onRetained() {
        if (triggerFirstEffect) {
            applyToTarget(adp(), adp(), new WeakPower(adp(), magicNumber, true));
        } else {
            applyToTarget(adp(), adp(), new FrailPower(adp(), magicNumber, true));
        }
        triggerFirstEffect = !triggerFirstEffect;
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_DEBUFF);
    }
}