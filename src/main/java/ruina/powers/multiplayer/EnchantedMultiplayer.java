package ruina.powers.multiplayer;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyMonster;
import ruina.powers.AbstractEasyPower;

import static ruina.util.Wiz.*;

public class EnchantedMultiplayer extends AbstractEasyPower {

    public static final String POWER_ID = RuinaMod.makeID(EnchantedMultiplayer.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public EnchantedMultiplayer(AbstractCreature owner, int amount, int amount2) {
        super(NAME, POWER_ID, PowerType.DEBUFF, false, owner, amount);
        this.amount2 = amount2;
        this.priority = 100;
        updateDescription();
        setPowerImage("Enchanted");
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
        applyToTarget(owner, adp(), new EnchantedMultiplayer(owner, damageAmount, 1));
        return damageAmount;
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount -= stackAmount;
        updateDescription();
        if (this.amount <= 0) {
            this.amount = 0;
            atb(new RemoveSpecificPowerAction(owner, owner, this));
        }
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return amount2;
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
        description = DESCRIPTIONS[0] + amount2 + DESCRIPTIONS[1] + amount + DESCRIPTIONS[2];
    }
}
