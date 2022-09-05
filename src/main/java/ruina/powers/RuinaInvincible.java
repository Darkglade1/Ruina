package ruina.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.InvinciblePower;

public class RuinaInvincible extends InvinciblePower {

    public RuinaInvincible(AbstractCreature owner, int amount) {
        super(owner, amount);
    }

    @Override
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

    @Override
    public void updateDescription() {
        if (this.amount <= 0) {
            this.description = DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        }

    }
}