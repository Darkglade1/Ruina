package ruina.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;

public class MirageStrength extends AbstractUnremovablePower {

    public static final String POWER_ID = RuinaMod.makeID(MirageStrength.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public MirageStrength(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        updateDescription();
        loadRegion("strength");
        canGoNegative = true;
    }

    @Override
    public void updateDescription() {
        if (amount > 0) {
            this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[1] + -amount + DESCRIPTIONS[2];
        }
    }

    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        return type == DamageInfo.DamageType.NORMAL ? damage + (float)this.amount : damage;
    }
}
