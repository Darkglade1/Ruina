package ruina.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.eventboss.redMist.monster.RedMist;

import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public class RedMistPower extends AbstractEasyPower {

    public static final String POWER_ID = RuinaMod.makeID(RedMistPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public RedMistPower(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, owner.maxHealth / 2);
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        if(owner.currentHealth - damageAmount <= amount){
            owner.currentHealth = amount;
            owner.healthBarUpdatedEvent();
            updateDescription();
            return 0;
        }
        return damageAmount;
    }
    public void EGOTrigger() {
        if(owner.currentHealth == amount){
            if(owner instanceof RedMist){ ((RedMist) owner).activateEGO(); }
            att(new RemoveSpecificPowerAction(owner, owner, this));
        }
    }
    public void updateDescription() { this.description = owner.currentHealth == amount ? DESCRIPTIONS[1] : String.format(DESCRIPTIONS[0], amount, amount) ; }

}