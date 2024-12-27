package ruina.powers.multiplayer;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class ExpressionMultiplayer extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(ExpressionMultiplayer.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final int CARD_THRESHOLD;

    public ExpressionMultiplayer(AbstractCreature owner, int amount, int amount2) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.CARD_THRESHOLD = amount2;
        setPowerImage("Expression");
        updateDescription();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        applyToTarget(owner, adp(), new ExpressionMultiplayer(owner, 1, CARD_THRESHOLD));
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        if (this.amount >= CARD_THRESHOLD) {
            this.amount = 0;
            trigger();
        }
    }

    private void trigger() {
        if (owner instanceof AbstractMonster) {
            flash();
            ((AbstractMonster) owner).rollMove();
            ((AbstractMonster) owner).createIntent();
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + CARD_THRESHOLD + POWER_DESCRIPTIONS[1];
    }
}
