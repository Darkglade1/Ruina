package ruina.powers.act4;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.cardmods.BrainwashMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.atb;

public class Brainwash extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Brainwash.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final int numTriggers;

    public Brainwash(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.amount = numTriggers = amount;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (this.amount > 0 && card.target == AbstractCard.CardTarget.ENEMY && (card.type == AbstractCard.CardType.ATTACK || card.type == AbstractCard.CardType.SKILL)) {
            this.flash();
            this.amount--;
            if (!CardModifierManager.hasModifier(card, BrainwashMod.ID)) {
                BrainwashMod mod = new BrainwashMod();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        CardModifierManager.addModifier(card, mod.makeCopy());
                        this.isDone = true;
                    }
                });
            }
        }
    }

    @Override
    public void atEndOfRound() {
        this.amount = numTriggers;
    }

    @Override
    public void updateDescription() {
        String num = "";
        if (numTriggers > 1) {
            num += "#b" + numTriggers + " ";
        }
        description = DESCRIPTIONS[0] + num + DESCRIPTIONS[1];
    }
}
