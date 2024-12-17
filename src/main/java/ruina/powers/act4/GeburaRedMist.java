package ruina.powers.act4;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.monsters.uninvitedGuests.normal.tanya.Gebura;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.applyToTarget;

public class GeburaRedMist extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(GeburaRedMist.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public GeburaRedMist(AbstractCreature owner, int amount, int EGOtimer) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        amount2 = EGOtimer;
        updateDescription();
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL && target != owner) {
            applyToTarget(owner, owner, new StrengthPower(owner, amount));
        }
    }

    @Override
    public void atEndOfRound() {
        if (owner instanceof Gebura) {
            if (!((Gebura) owner).manifestedEGO) {
                amount2--;
                if (amount2 <= 0) {
                    ((Gebura) owner).manifestEGO();
                }
                updateDescription();
            }
        }
    }

    @Override
    public void updateDescription() {
        if (amount2 > 0) {
            description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + POWER_DESCRIPTIONS[2] + amount2 + POWER_DESCRIPTIONS[3];
        } else {
            description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
        }
    }
}
