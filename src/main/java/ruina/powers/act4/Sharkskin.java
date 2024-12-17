package ruina.powers.act4;

import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.atb;

public class Sharkskin extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Sharkskin.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final int debuffCleanseTurns;

    public Sharkskin(AbstractCreature owner, int amount, int damageReduction, int debuffCleanseTurns) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        amount2 = damageReduction;
        this.debuffCleanseTurns = debuffCleanseTurns;
        updateDescription();
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL && !hasDebuff()) {
            return damage * (1.0f - ((float)amount2 / 100));
        } else {
            return damage;
        }
    }

    private boolean hasDebuff() {
        for (AbstractPower po : owner.powers) {
            if (po.type == PowerType.DEBUFF) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void atEndOfRound() {
        amount++;
        if (amount >= debuffCleanseTurns) {
            amount = 0;
            flash();
            atb(new RemoveDebuffsAction(owner));
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount2 + POWER_DESCRIPTIONS[1] + debuffCleanseTurns + POWER_DESCRIPTIONS[2];
    }
}
