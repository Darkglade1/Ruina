package ruina.powers.multiplayer;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.*;

public class FearMultiplayer extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(FearMultiplayer.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final int strLoss;
    private final int cardThreshold;

    public FearMultiplayer(AbstractCreature owner, int strLoss, int cardThreshold, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.strLoss = strLoss;
        this.cardThreshold = cardThreshold;
        setPowerImage("Fear");
        updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            applyToTarget(owner, adp(), new FearMultiplayer(owner, strLoss, cardThreshold, 1));
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        if (this.amount >= cardThreshold) {
            amount = 0;
            trigger();
        }
    }

    private void trigger() {
        this.flash();
        atb(new ApplyPowerAction(this.owner, adp(), new StrengthPower(this.owner, -strLoss), -strLoss));
        if (!this.owner.hasPower(ArtifactPower.POWER_ID)) {
            atb(new ApplyPowerAction(this.owner, adp(), new GainStrengthPower(this.owner, strLoss), strLoss));
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + cardThreshold + POWER_DESCRIPTIONS[1] + strLoss + POWER_DESCRIPTIONS[2];
    }
}
