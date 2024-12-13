package ruina.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.CustomIntent.IntentEnums;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyMonster;

public class MultiplayerAllyBuff extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(MultiplayerAllyBuff.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public MultiplayerAllyBuff(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        this.amount = (RuinaMod.getMultiplayerEnemyHealthScaling(100) - 100);
        updateDescription();
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        boolean shouldAffect = true;
        if (owner instanceof AbstractAllyMonster) {
            if (((AbstractAllyMonster) owner).intent == IntentEnums.MASS_ATTACK && ((AbstractAllyMonster) owner).massAttackHitsPlayer) {
                shouldAffect = false;
            }
        }
        if (type == DamageInfo.DamageType.NORMAL && shouldAffect) {
            return damage * (1.0f + ((float)amount / 100));
        } else {
            return damage;
        }
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        boolean isTargetableByPlayer = false;
        if (owner instanceof AbstractAllyMonster) {
            isTargetableByPlayer = ((AbstractAllyMonster) owner).isTargetableByPlayer;
        }
        if (type == DamageInfo.DamageType.NORMAL && !isTargetableByPlayer) {
            return damage * (1.0f + ((float)amount / 100));
        } else {
            return damage;
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + amount + POWER_DESCRIPTIONS[2];
    }
}
