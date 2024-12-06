package ruina.powers.act2;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;

import static ruina.util.Wiz.adp;

public class Oblivion extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID("Oblivion");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Oblivion(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.DEBUFF, false, owner, amount);
    }

    @Override
    public void onInitialApplication() {
        if (owner == adp()) {
            adp().gameHandSize -= amount;
        }
    }

    @Override
    public void stackPower(int amount) {
        super.stackPower(amount);
        adp().gameHandSize -= amount;
    }

    @Override
    public void onRemove() {
        if (owner == adp()) {
            adp().gameHandSize += amount;
        }
    }

    @Override
    public void updateDescription() {
        if (amount == 1) {
            this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1] + DESCRIPTIONS[3];
        } else {
            this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[2] + DESCRIPTIONS[3];
        }
    }
}
