package ruina.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

public class DamageReductionInvincible extends AbstractUnremovablePower{
    public static final String POWER_ID = RuinaMod.makeID(DamageReductionInvincible.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final float damageReduction = 75f;
    private int max;

    public DamageReductionInvincible(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        canGoNegative = false;
        max = amount;
        loadRegion("heartDef");
        updateDescription();
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if(amount - damageAmount < 0){ amount = 0; }
        else { amount -= damageAmount; }
        updateDescription();
        return damageAmount;
    }

    @Override
    public void atStartOfTurn() {
        amount = max;
        updateDescription();
    }

    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        if(damage > amount && type == DamageInfo.DamageType.NORMAL){
            return amount + ((damage - amount) * (1 - damageReduction / 100f ));
        }
        else {
            return damage;
        }}

    @Override
    public void updateDescription() {
        this.description = amount != 0 ?
                String.format(DESCRIPTIONS[0], amount) + damageReduction + DESCRIPTIONS[1]
                : DESCRIPTIONS[2] + damageReduction + DESCRIPTIONS[3];
    }
}