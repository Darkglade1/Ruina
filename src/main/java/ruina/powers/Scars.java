package ruina.powers;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

import static ruina.util.Wiz.atb;

public class Scars extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID("Scars");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final int THRESHOLD = 5;

    public Scars(AbstractCreature owner, int healPercent) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 1);
        this.priority = 90;
        this.amount2 = healPercent;
        updateDescription();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            if (this.amount >= THRESHOLD) {
                this.amount = 1;
            } else {
                this.amount++;
            }
        }
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (this.amount >= THRESHOLD && info.type == DamageInfo.DamageType.NORMAL) {
            atb(new HealAction(owner, owner, (int)(damageAmount * ((float)amount2 / 100))));
            return 0;
        } else {
            return damageAmount;
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + THRESHOLD + DESCRIPTIONS[1] + amount2 + DESCRIPTIONS[2];
    }
}
