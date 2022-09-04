package ruina.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.InvinciblePower;

public class RuinaInvincible extends AbstractUnremovablePower {
    public static final String POWER_ID = InvinciblePower.POWER_ID;
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int maxAmt;

    public RuinaInvincible(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.maxAmt = amount;
        this.updateDescription();
        this.loadRegion("heartDef");
        this.priority = 99;
    }

    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (damageAmount > this.amount) {
            damageAmount = this.amount;
        }

        this.amount -= damageAmount;
        if (this.amount < 0) {
            this.amount = 0;
        }

        this.updateDescription();
        return damageAmount;
    }

    public void atStartOfTurn() {
        this.amount = this.maxAmt;
        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        if (this.amount <= 0) {
            this.description = DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        }

    }
}