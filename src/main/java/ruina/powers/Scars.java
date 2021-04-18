package ruina.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

public class Scars extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID("Scars");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final int THRESHOLD = 5;

    public Scars(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 1);
        this.priority = 99;
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
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (this.amount >= THRESHOLD && type == DamageInfo.DamageType.NORMAL) {
            return 0;
        } else {
            return damage;
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + THRESHOLD + DESCRIPTIONS[1];
    }
}
