package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;

import static ruina.util.Wiz.*;

public class SingularityJ extends AbstractUnremovablePower implements OnReceivePowerPower {
    public static final String POWER_ID = RuinaMod.makeID(SingularityJ.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SingularityJ(AbstractCreature owner) {
        super(NAME, POWER_ID, NeutralPowertypePatch.NEUTRAL, false, owner, 0);
    }

    @Override
    public void onInitialApplication() {
        addToBot(new RemoveDebuffsAction(owner));
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        return power.type != PowerType.DEBUFF;
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.owner == owner && owner != target && info.type == DamageInfo.DamageType.NORMAL) {
            if (target.hasPower(StrengthPower.POWER_ID)) {
                AbstractPower strength = target.getPower(StrengthPower.POWER_ID);
                if (strength.amount > 0) {
                    addToTop(new RemoveSpecificPowerAction(target, owner, StrengthPower.POWER_ID));
                    applyToTargetNextTurn(owner, new StrengthPower(owner, strength.amount));
                }
            }
        }
    }



    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}