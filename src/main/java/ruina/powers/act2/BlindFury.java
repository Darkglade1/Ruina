package ruina.powers.act2;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.act2.wrath.ServantOfWrath;
import ruina.powers.AbstractUnremovablePower;

import static ruina.monsters.AbstractRuinaMonster.playSound;

public class BlindFury extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(BlindFury.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int initialAmount;

    public BlindFury(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.initialAmount = amount;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (damageAmount > 0) {
            reducePower(damageAmount);
            updateDescription();
        }
        return damageAmount;
    }

    @Override
    public void atEndOfRound() {
        if (this.amount <= 0) {
            if (owner instanceof ServantOfWrath) {
                ((ServantOfWrath) owner).setPhase(ServantOfWrath.ENRAGE_PHASE);
                ((ServantOfWrath) owner).rollMove();
                ((ServantOfWrath) owner).createIntent();
                playSound("WrathMeet");
            }
            owner.addPower(new BlindFury(owner, initialAmount));
            updateDescription();
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        if (AbstractDungeon.actionManager.turnHasEnded) {
            this.fontScale = 8.0F;
            this.amount = stackAmount;
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
    }
}
