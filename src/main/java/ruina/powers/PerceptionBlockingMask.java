package ruina.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

public class PerceptionBlockingMask extends AbstractUnremovablePower {

    public static final String POWER_ID = RuinaMod.makeID("PerceptionBlockingMask");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public int CARDS_PER_TURN;

    public PerceptionBlockingMask(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        CARDS_PER_TURN = amount;
        updateDescription();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.amount++;
        if (this.amount >= CARDS_PER_TURN) {
            flash();
            this.amount = 0;
        } else {
            flashWithoutSound();
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + CARDS_PER_TURN + DESCRIPTIONS[1];
    }
}
