package ruina.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;

import static ruina.util.Wiz.*;

public class Pneumonia extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Pneumonia.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int palpitationAmount = 1;

    public Pneumonia(AbstractCreature owner, int newThreshold) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        priority = 2;
        amount2 = newThreshold;
        updateDescription();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        flash();
        amount++;
        if(amount >= amount2){
            atb(new ApplyPowerAction(owner, owner, new Palpitation(owner, 1)));
            amount -= amount2;
        }
        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = String.format(DESCRIPTIONS[0], amount2, palpitationAmount);
    }

    public void changeThreshold(int newThreshold){
        amount2 = newThreshold;
        updateDescription();
    }
}