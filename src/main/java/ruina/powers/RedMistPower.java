package ruina.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

public class RedMistPower extends AbstractEasyPower {

    public static final String POWER_ID = RuinaMod.makeID(RedMistPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public RedMistPower(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        amount = owner.maxHealth / 2;
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        if(owner.currentHealth - damageAmount <= amount){
            owner.currentHealth = amount;
            owner.healthBarUpdatedEvent();
            return 0;
        }
        return damageAmount;
    }

    public void updateDescription() { this.description = owner.currentHealth == amount ? DESCRIPTIONS[1] : DESCRIPTIONS[0]; }

}