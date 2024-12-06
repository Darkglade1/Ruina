package ruina.powers.act3;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

public class RedMistPower extends AbstractUnremovablePower {

    public static final String POWER_ID = RuinaMod.makeID(RedMistPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public RedMistPower(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, owner.maxHealth / 2);
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        if(owner.currentHealth - damageAmount <= amount){
            damageAmount =  owner.currentHealth - amount;
            updateDescription();
            return damageAmount;
        }
        return damageAmount;
    }
    public void updateDescription() { this.description = owner.currentHealth == amount ? DESCRIPTIONS[1] : String.format(DESCRIPTIONS[0], amount, amount) ; }

}