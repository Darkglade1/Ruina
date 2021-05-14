package ruina.powers;

import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.mod.stslib.actions.common.SelectCardsAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import ruina.cardmods.FrozenMod;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class FrostSplinter extends AbstractUnremovablePower {
    public static final String POWER_ID = makeID(FrostSplinter.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final int FREEZE_AMOUNT = 1;
    private static final int ENERGY_AMOUNT = 1;
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID(FrostSplinter.class.getSimpleName()));
    public FrostSplinter(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        amount2 = ENERGY_AMOUNT;
    }
    public void atStartOfTurnPostDraw(){
        atb(new GainEnergyAction(ENERGY_AMOUNT));
        atb(new DrawCardAction(owner, amount));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                att(new SelectCardsAction(adp().hand.group, FREEZE_AMOUNT, uiStrings.TEXT[0], (cards) -> {
                    AbstractCard c = cards.get(0);
                    CardModifierManager.addModifier(c, new FrozenMod());
                }));
                isDone = true;
            }
        });

    }
    @Override
    public void updateDescription() { description = String.format(amount > 1 ? DESCRIPTIONS[1] : DESCRIPTIONS[0], amount, FREEZE_AMOUNT); }
}