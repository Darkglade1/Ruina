package ruina.powers.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.atb;

public class Sin extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Sin.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int COST_INCREASE;

    public Sin(AbstractCreature owner, int amount, int costIncrease) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.COST_INCREASE = costIncrease;
        updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.costForTurn <= amount) {
            this.flash();
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    card.modifyCostForCombat(COST_INCREASE);
                    this.isDone = true;
                }
            });

        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + COST_INCREASE + POWER_DESCRIPTIONS[2];
    }
}
