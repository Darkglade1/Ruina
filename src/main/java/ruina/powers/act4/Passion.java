package ruina.powers.act4;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.makePowerRemovable;

public class Passion extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Passion.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Passion(AbstractCreature owner, int amount, int damageReductionDecay) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.amount2 = damageReductionDecay;
        updateDescription();
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * (1.0f - ((float)amount / 100));
        } else {
            return damage;
        }
    }

    @Override
    public void atEndOfRound() {
        if (amount <= amount2) {
            makePowerRemovable(this);
            makePowerRemovable(owner, FlameShield.POWER_ID);
            atb(new RemoveSpecificPowerAction(owner, owner, FlameShield.POWER_ID));
        }
        atb(new ReducePowerAction(owner, owner, this, amount2));
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + amount2 + POWER_DESCRIPTIONS[2];
    }
}
