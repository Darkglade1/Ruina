package ruina.powers;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

import static ruina.util.Wiz.atb;

public class ColdHearted extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(ColdHearted.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ColdHearted(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
        if (amount == 1) {
            description += String.format(DESCRIPTIONS[1], amount);
        } else {
            description += String.format(DESCRIPTIONS[2], amount);
        }
    }

    public void atStartOfTurn() {
        if (this.amount == 0) {
            atb(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        } else {
            atb(new ReducePowerAction(this.owner, this.owner, this, 1));
        }
    }

    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) { return damage * 0.5F; }
        return damage;
    }
}