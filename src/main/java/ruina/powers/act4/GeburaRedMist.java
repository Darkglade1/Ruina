package ruina.powers.act4;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.monsters.uninvitedGuests.normal.tanya.Gebura;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class GeburaRedMist extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(GeburaRedMist.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int strGain;

    public GeburaRedMist(AbstractCreature owner, int strGain, int EGOtimer) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, EGOtimer);
        this.strGain = strGain;
        updateDescription();
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL && target != owner) {
            applyToTarget(owner, owner, new StrengthPower(owner, strGain));
        }
    }

    @Override
    public void atEndOfRound() {
        if (owner instanceof Gebura) {
            if (((Gebura) owner).phase == ((Gebura) owner).DEFAULT_PHASE) {
                if (amount == 1) {
                    amount = 0;
                    ((Gebura) owner).manifestEGO();
                    updateDescription();
                } else {
                    atb(new ReducePowerAction(owner, owner, this, 1));
                }
            }
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        // doesn't stack
    }

    @Override
    public void updateDescription() {
        if (amount > 0) {
            description = POWER_DESCRIPTIONS[0] + strGain + POWER_DESCRIPTIONS[1] + POWER_DESCRIPTIONS[2] + amount + POWER_DESCRIPTIONS[3];
        } else {
            description = POWER_DESCRIPTIONS[0] + strGain + POWER_DESCRIPTIONS[1];
        }
    }
}
