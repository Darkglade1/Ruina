package ruina.powers.act1;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;
import ruina.powers.Burn;

import static ruina.util.Wiz.applyToTarget;

public class FlamingBat extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(FlamingBat.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FlamingBat(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
            applyToTarget(target, owner, new Burn(target, amount));
        }
    }

    @Override
    public void updateDescription() {
        description = String.format(POWER_DESCRIPTIONS[0], amount);
    }
}
