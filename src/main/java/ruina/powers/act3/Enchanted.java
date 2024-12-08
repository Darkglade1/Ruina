package ruina.powers.act3;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyMonster;
import ruina.powers.AbstractEasyPower;

import static ruina.util.Wiz.atb;

public class Enchanted extends AbstractEasyPower {

    public static final String POWER_ID = RuinaMod.makeID("Enchanted");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Enchanted(AbstractCreature owner, int amount, int amount2) {
        super(NAME, POWER_ID, PowerType.DEBUFF, false, owner, amount);
        this.amount2 = amount2;
        this.priority = 100;
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        if (owner instanceof AbstractAllyMonster) {
            ((AbstractAllyMonster) owner).isTargetableByPlayer = true;
            owner.halfDead = false;
        }
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        amount2 -= damageAmount;
        if (amount2 <= 0) {
            atb(new RemoveSpecificPowerAction(owner, owner, this));
        }
        updateDescription();
        return damageAmount;
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return amount;
        } else {
            return damage;
        }
    }

    @Override
    public void onRemove() {
        flash();
        if (owner instanceof AbstractAllyMonster) {
            ((AbstractAllyMonster) owner).isTargetableByPlayer = false;
            owner.halfDead = true;
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1] + amount2 + DESCRIPTIONS[2];
    }
}
