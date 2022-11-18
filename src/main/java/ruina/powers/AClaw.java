package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;

import static ruina.util.Wiz.applyToTargetTop;

public class AClaw extends AbstractUnremovablePower implements OnReceivePowerPower {
    public static final String POWER_ID = RuinaMod.makeID(AClaw.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private boolean active = true;
    private int kill_threshold;

    public AClaw(AbstractCreature owner, int kill_threshold) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        this.kill_threshold = kill_threshold;
        updateDescription();
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if ((power instanceof StrengthPower && power.amount < 0) || (power instanceof GainStrengthPower && power.amount > 0)) {
            return false;
        }
        return true;
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (target == owner && power instanceof StrengthPower) {
            if(active) {
                active = false;
                this.addToTop(new AbstractGameAction() {
                    @Override
                    public void update() {
                        this.isDone = true;
                        active = true;
                    }
                });
                this.flash();
                int bonusAmount = power.amount;
                applyToTargetTop(owner, owner, new StrengthPower(owner, bonusAmount));
            }
        }
    }

    @Override
    public void updateDescription() { this.description = DESCRIPTIONS[0] + kill_threshold + DESCRIPTIONS[1]; }
}