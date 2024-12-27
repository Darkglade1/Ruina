package ruina.powers.act1;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.cards.Gift;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.*;
import static ruina.util.Wiz.adp;

public class Gimme extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Gimme.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Gimme(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void onDeath() {
        this.flash();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractCard card : adp().hand.group) {
                    if (card instanceof Gift) {
                        att(new ExhaustSpecificCardAction(card, adp().hand));
                    }
                }
                this.isDone = true;
            }
        });
    }
    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0];
    }
}
