package ruina.powers.act2;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.act2.BadWolf;
import ruina.powers.AbstractEasyPower;

public class Hunter extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(Hunter.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int STRENGTH;
    private final float HP_THRESHOLD;

    public Hunter(AbstractCreature owner, int amount, int strength, float hp_threshold) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.STRENGTH = strength;
        this.HP_THRESHOLD = hp_threshold;
        updateDescription();
    }

    @Override
    public void atEndOfRound() {
        if (owner instanceof BadWolf) {
            ((BadWolf) owner).checkSkulkTrigger();
        }
    }

    @Override
    public void updateDescription() {
        if (amount == 1) {
            description = POWER_DESCRIPTIONS[0] + (int)(HP_THRESHOLD * 100) + POWER_DESCRIPTIONS[1] + STRENGTH + POWER_DESCRIPTIONS[2] + amount + POWER_DESCRIPTIONS[4];
        } else {
            description = POWER_DESCRIPTIONS[0] + (int)(HP_THRESHOLD * 100) + POWER_DESCRIPTIONS[1] + STRENGTH + POWER_DESCRIPTIONS[2] + amount + POWER_DESCRIPTIONS[3];
        }
    }
}
