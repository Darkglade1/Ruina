package ruina.powers.act2;

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

import static ruina.util.Wiz.atb;

public class CounterAttack extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(CounterAttack.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    boolean justApplied = true;

    public CounterAttack(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK && !owner.hasPower(StunMonsterPower.POWER_ID) && owner instanceof Kim) {
            this.flash();
            ((Kim) owner).CounterAttack();
            ((Kim) owner).usedCounter = true;
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
        description = POWER_DESCRIPTIONS[0];
    }
}
