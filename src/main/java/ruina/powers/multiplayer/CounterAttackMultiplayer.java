package ruina.powers.multiplayer;

import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.eventboss.kim.Kim;
import ruina.powers.AbstractUnremovablePower;
import ruina.util.Wiz;

import static ruina.util.Wiz.*;

public class CounterAttackMultiplayer extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(CounterAttackMultiplayer.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    boolean justApplied = true;
    private final int threshold;

    public CounterAttackMultiplayer(AbstractCreature owner, int threshold, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.threshold = threshold;
        setPowerImage("CounterAttack");
        updateDescription();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK && !owner.hasPower(StunMonsterPower.POWER_ID)) {
            applyToTarget(owner, adp(), new CounterAttackMultiplayer(owner, threshold, 1));
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        if (this.amount >= threshold) {
            this.amount = 0;
            onSpecificTrigger();
        }
    }

    @Override
    public void onSpecificTrigger() {
        if (owner instanceof Kim) {
            this.flash();
            ((Kim) owner).CounterAttack();
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (justApplied) {
            justApplied = false;
        } else {
            if (!owner.hasPower(StunMonsterPower.POWER_ID)) {
                Wiz.makePowerRemovable(this);
                atb(new RemoveSpecificPowerAction(owner, owner, this));
            }
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + threshold + POWER_DESCRIPTIONS[1];
    }
}
