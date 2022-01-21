package ruina.cards.performance;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTargetTop;

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
        updateDescription();
    }

    @Override
    public void EndOfTurnEffect() {
        if (triggerFirstEffect) {
            applyToTargetTop(adp(), adp(), new WeakPower(adp(), magicNumber, true));
        } else {
            applyToTargetTop(adp(), adp(), new FrailPower(adp(), magicNumber, true));
        }
        triggerFirstEffect = !triggerFirstEffect;
        updateDescription();
    }

    private void updateDescription() {
        if (triggerFirstEffect) {
            this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
        } else {
            this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[1];
        }
        this.initializeDescription();
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_DEBUFF);
    }
}