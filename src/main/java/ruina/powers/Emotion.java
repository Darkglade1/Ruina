package ruina.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.monsters.uninvitedGuests.philip.Malkuth;

import static ruina.util.Wiz.applyToTarget;

public class Emotion extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(Emotion.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int threshold;

    public Emotion(AbstractCreature owner, int amount, int threshold) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        amount2 = 0;
        this.threshold = threshold;
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        checkTrigger();
    }

    public void checkTrigger() {
        if ((amount / threshold) >= 1) {
            this.flash();
            stackPower(-threshold);
            amount2++;

            AbstractPower str = owner.getPower(StrengthPower.POWER_ID);
            if (str != null) {
                applyToTarget(owner, owner, new StrengthPower(owner, str.amount));
            }
        }
        updateDescription();
    }

    @Override
    public void atEndOfRound() {
        if (owner instanceof Malkuth) {
            if (amount2 >= Malkuth.secondEmotionThreshold) {
                ((Malkuth) owner).manifest();
            }
            if (amount2 >= Malkuth.firstEmotionThreshold) {
                ((Malkuth) owner).distort();
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount2 + DESCRIPTIONS[1] + threshold + DESCRIPTIONS[2] + Malkuth.firstEmotionThreshold + DESCRIPTIONS[3] + Malkuth.secondEmotionThreshold + DESCRIPTIONS[4];
    }
}