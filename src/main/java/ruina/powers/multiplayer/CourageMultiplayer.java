package ruina.powers.multiplayer;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class CourageMultiplayer extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(CourageMultiplayer.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    AbstractCreature road;
    private final int threshold;
    private final int strength;

    public CourageMultiplayer(AbstractCreature owner, int strength, int threshold, int amount, AbstractCreature road) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.road = road;
        this.threshold = threshold;
        this.strength = strength;
        setPowerImage("Courage");
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        if (this.amount >= threshold) {
            this.amount = 0;
            trigger();
        }
    }

    @Override
    public void onSpecificTrigger() {
        applyToTarget(owner, adp(), new CourageMultiplayer(owner, strength, threshold, 1, road));
    }

    private void trigger() {
        flash();
        applyToTarget(owner, owner, new StrengthPower(owner, strength));
        applyToTarget(owner, owner, new LoseStrengthPower(owner, strength));
    }

    @Override
    public void atEndOfRound() {
        amount = 0;
    }

    @Override
    public void updateDescription() {
        if (road != null) {
            description = POWER_DESCRIPTIONS[0] + FontHelper.colorString(road.name, "y") + POWER_DESCRIPTIONS[1] + threshold + POWER_DESCRIPTIONS[2] + strength + POWER_DESCRIPTIONS[3];
        }
    }
}
