package ruina.powers.act2;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.act2.mountain.Mountain;
import ruina.powers.AbstractEasyPower;

public class Absorption extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(Absorption.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Absorption(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void updateDescription() {
        if (amount == Mountain.STAGE1) {
            description = POWER_DESCRIPTIONS[2] + (amount + 1) + POWER_DESCRIPTIONS[3];
        } else if (amount == Mountain.STAGE2) {
            description = POWER_DESCRIPTIONS[0] + (amount - 1) + POWER_DESCRIPTIONS[1] + " " + POWER_DESCRIPTIONS[2] + (amount + 1) + POWER_DESCRIPTIONS[3];
        } else {
            description = POWER_DESCRIPTIONS[0] + (amount - 1) + POWER_DESCRIPTIONS[1];
        }
    }
    @Override
    public void atEndOfRound() {
        if (owner.currentHealth == owner.maxHealth) {
            if (owner instanceof Mountain) {
                ((Mountain) owner).Grow();
            }
        }
    }
}
