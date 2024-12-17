package ruina.powers.act4;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.atb;

public class Mark extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Mark.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    boolean justApplied = true;
    float markBoost;

    public Mark(AbstractCreature owner, int markDuration, float markBoost) {
        super(NAME, POWER_ID, PowerType.DEBUFF, false, owner, markDuration);
        this.markBoost = markBoost;
        updateDescription();
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        //handles attack damage
        if (type == DamageInfo.DamageType.NORMAL) {
            return calculateDamageTakenAmount(damage, type);
        } else {
            return damage;
        }
    }

    private float calculateDamageTakenAmount(float damage, DamageInfo.DamageType type) {
        return damage * (1 + markBoost);
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        //handles non-attack damage
        if (info.type != DamageInfo.DamageType.NORMAL) {
            return (int) calculateDamageTakenAmount(damageAmount, info.type);
        } else {
            return damageAmount;
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        justApplied = true;
    }

    @Override
    public void atEndOfRound() {
        if (justApplied) {
            justApplied = false;
        } else {
            atb(new ReducePowerAction(owner, owner, this, 1));
        }
    }

    @Override
    public void updateDescription() {
        this.description = POWER_DESCRIPTIONS[0] + (int)(markBoost * 100) + POWER_DESCRIPTIONS[1] + amount + POWER_DESCRIPTIONS[2];
    }
}
