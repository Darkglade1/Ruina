package ruina.powers;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

import static ruina.util.Wiz.atb;

public class WingsOfGrace extends AbstractEasyPower{
    public static final String POWER_ID = RuinaMod.makeID(WingsOfGrace.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public WingsOfGrace(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);

    }

    public void playApplyPowerSfx() { CardCrawlGame.sound.play("POWER_FLIGHT", 0.05F); }

    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) { return calculateDamageTakenAmount(damage, type); }

    private float calculateDamageTakenAmount(float damage, DamageInfo.DamageType type) {
        return damage / 2.0F;
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        boolean willLive = (calculateDamageTakenAmount(damageAmount, info.type) < this.owner.currentHealth);
        if (info.owner != null && damageAmount > 0 && willLive) {
            flash();
            atb(new ReducePowerAction(this.owner, this.owner, this, 1));
        }
        return damageAmount;
    }

    @Override
    public void updateDescription() { this.description = DESCRIPTIONS[0]; }
}