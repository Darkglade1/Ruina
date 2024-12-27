package ruina.powers.multiplayer;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class SenselessWrathMultiplayer extends AbstractUnremovablePower implements OnReceivePowerPower {

    public static final String POWER_ID = RuinaMod.makeID(SenselessWrathMultiplayer.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final int THRESHOLD = 2;
    private boolean justStrengthDown = false;
    private boolean negateGainStrengthUp = false;

    public SenselessWrathMultiplayer(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        setPowerImage("SenselessWrath");
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (power.type == PowerType.DEBUFF) {
            //Handle temp Strength down effects
            if (power instanceof StrengthPower) {
                if (this.amount < THRESHOLD) {
                    justStrengthDown = true;
                } else {
                    negateGainStrengthUp = true;
                }
            }
            if (power instanceof GainStrengthPower) {
                if (justStrengthDown) {
                    justStrengthDown = false;
                    return true;
                }
                if (negateGainStrengthUp) {
                    negateGainStrengthUp = false;
                    return false;
                }
            }
            //Actual code
            applyToTarget(owner, adp(), new SenselessWrathMultiplayer(owner, 1));
            if (this.amount >= THRESHOLD) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        if (this.amount >= THRESHOLD) {
            this.amount = 1;
            justStrengthDown = false;
        } else {
            this.amount += stackAmount;
            negateGainStrengthUp = false;
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}
