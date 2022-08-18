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
import ruina.monsters.day49.Act1Angela;
import ruina.monsters.day49.Act2Angela;
import ruina.monsters.day49.Act4Angela;

import static ruina.util.Wiz.*;

public class Palpitation extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Palpitation.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int palpitationStrength = 1;

    public Palpitation(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        amount2 = palpitationStrength;
        priority = 1;
        updateDescription();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        flash();
        atb(new DamageAction(adp(), new DamageInfo(owner, amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if(adp().lastDamageTaken > 0){ att(new ApplyPowerAction(owner, owner, new StrengthPower(owner, amount2))); }
                isDone = true;
            }
        });
        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = String.format(DESCRIPTIONS[0], amount, amount2);
    }
}