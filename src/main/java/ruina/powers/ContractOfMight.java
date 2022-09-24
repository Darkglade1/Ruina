package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.VelvetChoker;
import ruina.RuinaMod;

public class ContractOfMight extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(ContractOfMight.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(VelvetChoker.ID);
    public static final String[] CHOKER_DESCRIPTIONS = relicStrings.DESCRIPTIONS;

    private final int cardLimit;

    public ContractOfMight(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, NeutralPowertypePatch.NEUTRAL, false, owner, 0);
        this.cardLimit = amount;
        updateDescription();
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        if (this.amount >= cardLimit) {
            card.cantUseMessage = CHOKER_DESCRIPTIONS[3] + cardLimit + CHOKER_DESCRIPTIONS[1];
            return false;
        }
        return super.canPlayCard(card);
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.flashWithoutSound();
        this.amount++;
    }

    @Override
    public void atStartOfTurn() {
        this.amount = 0;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + cardLimit + DESCRIPTIONS[1];
    }

}
