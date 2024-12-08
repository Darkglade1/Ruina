package ruina.powers.act5;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

public class SingularityJ extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(SingularityJ.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    protected static final int HP_LIMIT = 500;

    AbstractMonster otherEnemy;

    public SingularityJ(AbstractCreature owner, AbstractMonster otherEnemy) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, HP_LIMIT);
        this.otherEnemy = otherEnemy;
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (owner.currentHealth - damageAmount <= otherEnemy.currentHealth - amount) {
            damageAmount =  owner.currentHealth - (otherEnemy.currentHealth - amount);
        }
        return damageAmount;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}