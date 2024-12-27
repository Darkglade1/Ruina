package ruina.powers.act3;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.cardmods.FrozenMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.atb;

public class PromiseOfWinter extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(PromiseOfWinter.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int THRESHOLD;

    public PromiseOfWinter(AbstractCreature owner, int amount, int threshold) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.THRESHOLD = threshold;
        updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        this.amount++;
        if (this.amount >= THRESHOLD) {
            this.flash();
            FrozenMod mod = new FrozenMod();
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    if (!CardModifierManager.hasModifier(card, FrozenMod.ID)) {
                        CardModifierManager.addModifier(card, mod.makeCopy());
                    }
                    this.isDone = true;
                }
            });
            this.amount = 0;
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0];
    }
}
