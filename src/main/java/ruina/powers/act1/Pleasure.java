package ruina.powers.act1;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;

import static ruina.util.Wiz.atb;

public class Pleasure extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID("Pleasure");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Pleasure(AbstractCreature owner, int amount, int initialEnergy) {
        super(NAME, POWER_ID, PowerType.DEBUFF, false, owner, amount);
        this.amount = amount;
        this.amount2 = initialEnergy;
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        flash();
        atb(new GainEnergyAction(this.amount2));
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        flash();
        atb(new DamageAction(owner, new DamageInfo(owner, amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.POISON));
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        amount2 += 1;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(DESCRIPTIONS[0]);

        for(int i = 0; i < this.amount2; ++i) {
            sb.append("[E] ");
        }

        sb.append(LocalizedStrings.PERIOD);
        this.description = sb.toString() + DESCRIPTIONS[1] + amount + DESCRIPTIONS[2];

    }
}
