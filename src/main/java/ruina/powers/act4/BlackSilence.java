package ruina.powers.act4;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;

public class BlackSilence extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(BlackSilence.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BlackSilence(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        flash();
        addToBot(new ApplyPowerAction(owner, owner, new StrengthPower(owner, amount), amount));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
        if (amount2 == 0) {
            description += DESCRIPTIONS[5];
        } else if (amount2 == 1) {
            description += DESCRIPTIONS[2] + amount2 + DESCRIPTIONS[4];
        } else {
            description += DESCRIPTIONS[2] + amount2 + DESCRIPTIONS[3];
        }
    }
}