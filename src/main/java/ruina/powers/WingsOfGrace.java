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

    private static final float DAMAGE_REDUCTION = 0.5f;
    private static final int REDUCTION = 1;

    public WingsOfGrace(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.priority = 50;
    }

    public void playApplyPowerSfx() { CardCrawlGame.sound.play("POWER_FLIGHT", 0.05F); }

    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) { return calculateDamageTakenAmount(damage, type); }

    private float calculateDamageTakenAmount(float damage, DamageInfo.DamageType type) {
        return damage * (1 - DAMAGE_REDUCTION);
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        atb(new ReducePowerAction(this.owner, this.owner, this, REDUCTION));
        return damageAmount;
    }

    @Override
    public void updateDescription() { this.description = DESCRIPTIONS[0] + (int)(DAMAGE_REDUCTION * 100) + DESCRIPTIONS[1] + REDUCTION + DESCRIPTIONS[2]; }
}