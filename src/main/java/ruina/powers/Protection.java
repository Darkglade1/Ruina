package ruina.powers;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

import static ruina.util.Wiz.atb;

public class Protection extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(Protection.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justApplied = true;
    private static final int DECAY = 1;

    public Protection(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.priority = 0; //make it apply before vulnerable
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        return type == DamageInfo.DamageType.NORMAL ? damage - (float)this.amount : damage;
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        justApplied = true;
    }

    @Override
    public void atEndOfRound() {
        if (justApplied) {
            justApplied = false;
        } else {
            atb(new ReducePowerAction(owner, owner, this, DECAY));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1] + DECAY + DESCRIPTIONS[2];
    }
}
