package ruina.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class DrawReductionNextTurn extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(DrawReductionNextTurn.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public DrawReductionNextTurn(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.DEBUFF, false, owner, amount);
        this.loadRegion("lessdraw");
    }

    @Override
    public void onInitialApplication() {
        adp().gameHandSize -= amount;
    }

    @Override
    public void onRemove() {
        adp().gameHandSize += amount;
    }

    @Override
    public void atStartOfTurnPostDraw() {
        this.flash();
        atb(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}
