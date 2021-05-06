package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;

import static ruina.util.Wiz.applyToTarget;

public class AClaw extends AbstractUnremovablePower implements OnReceivePowerPower {
    public static final String POWER_ID = RuinaMod.makeID(AClaw.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public final int DAMAGE_REDUCTION;
    public final int STRENGTH;

    public AClaw(AbstractCreature owner, int amount, int damageReduction, int strength) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        DAMAGE_REDUCTION = damageReduction;
        STRENGTH = strength;
        updateDescription();
        this.priority = 99;
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if ((power instanceof StrengthPower && power.amount < 0) || (power instanceof GainStrengthPower && power.amount > 0)) {
            return false;
        }
        return true;
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (damage >= amount && type == DamageInfo.DamageType.NORMAL) {
            return (damage * (1 - ((float)DAMAGE_REDUCTION / 100)));
        }
        return damage;
    }

    @Override
    public void onSpecificTrigger() {
        flash();
        applyToTarget(owner, owner, new StrengthPower(owner, STRENGTH));
    }

    @Override
    public void updateDescription() { this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1] + DAMAGE_REDUCTION + DESCRIPTIONS[2] + STRENGTH + DESCRIPTIONS[3]; }
}