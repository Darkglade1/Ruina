package ruina.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class FerventAdoration extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID("FerventAdoration");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FerventAdoration(AbstractCreature owner, int amount, int initialDraw) {
        super(NAME, POWER_ID, PowerType.DEBUFF, false, owner, amount);
        this.amount = amount;
        this.amount2 = initialDraw;
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        if (owner == adp()) {
            adp().gameHandSize += amount2;
        }
    }

    @Override
    public void onRemove() {
        if (owner == adp()) {
            adp().gameHandSize -= amount2;
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        flash();
        atb(new DamageAction(owner, new DamageInfo(owner, amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        if (owner == adp()) {
            adp().gameHandSize += 1;
        }
        amount2 += 1;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (amount2 == 1) {
            this.description = DESCRIPTIONS[0] + amount2 + DESCRIPTIONS[1] + DESCRIPTIONS[3] + amount + DESCRIPTIONS[4];
        } else {
            this.description = DESCRIPTIONS[0] + amount2 + DESCRIPTIONS[2] + DESCRIPTIONS[3] + amount + DESCRIPTIONS[4];
        }
    }
}
