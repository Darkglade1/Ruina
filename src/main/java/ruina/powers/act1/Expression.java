package ruina.powers.act1;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;

public class Expression extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(Expression.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final int CARD_THRESHOLD;

    public Expression(AbstractCreature owner, int amount, int amount2) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.CARD_THRESHOLD = amount2;
        updateDescription();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.amount++;
        if (this.amount >= CARD_THRESHOLD) {
            flash();
            this.amount = 0;
            if (owner instanceof AbstractMonster) {
                ((AbstractMonster) owner).rollMove();
                ((AbstractMonster) owner).createIntent();
            }
        } else {
            flashWithoutSound();
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + CARD_THRESHOLD + POWER_DESCRIPTIONS[1];
    }
}
