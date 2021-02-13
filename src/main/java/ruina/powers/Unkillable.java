package ruina.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

import static ruina.util.Wiz.atb;

public class Unkillable extends AbstractEasyPower{
    public static final String POWER_ID = RuinaMod.makeID(Unkillable.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Unkillable(AbstractCreature owner) { super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1); }

    @Override
    public void updateDescription() { this.description = DESCRIPTIONS[0]; }

    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) { return calculateDamageTakenAmount(damage, type); }

    private float calculateDamageTakenAmount(float damage, DamageInfo.DamageType type) { return damage; }

    public int onAttacked(DamageInfo info, int damageAmount) {
        boolean willLive = (calculateDamageTakenAmount(damageAmount, info.type) < this.owner.currentHealth);
        if (!willLive) {
            flash();
            owner.currentHealth = 1;
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    owner.healthBarRevivedEvent();
                    isDone = true;
                }
            });
            return 0;
        }
        return damageAmount;
    }
}