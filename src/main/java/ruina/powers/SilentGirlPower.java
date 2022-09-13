package ruina.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

public class SilentGirlPower extends AbstractUnremovablePower {

    public static final String POWER_ID = RuinaMod.makeID(SilentGirlPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int THRESHOLD;
    public SilentGirlPower(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        THRESHOLD = owner.maxHealth / 2;
        updateDescription();
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        if(owner.currentHealth - damageAmount <= THRESHOLD){
            damageAmount =  owner.currentHealth - THRESHOLD;
            updateDescription();
            return damageAmount;
        }
        return damageAmount;
    }
    public void updateDescription() { description = String.format(DESCRIPTIONS[0], THRESHOLD, THRESHOLD); }
}