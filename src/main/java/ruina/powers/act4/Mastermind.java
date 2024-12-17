package ruina.powers.act4;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

public class Mastermind extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Mastermind.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Mastermind(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
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
        if (owner.hasPower(Mark.POWER_ID)) {
            return damage;
        } else {
            return damage * (1 - ((float)amount / 100));
        }
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
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
    }
}
