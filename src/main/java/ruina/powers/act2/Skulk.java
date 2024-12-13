package ruina.powers.act2;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.act2.BadWolf;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.*;

public class Skulk extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Skulk.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Skulk(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void onInitialApplication() {
        att(new AbstractGameAction() {
            @Override
            public void update() {
                owner.halfDead = true;
                this.isDone = true;
            }
        });
        if (owner instanceof BadWolf) {
            ((BadWolf) owner).changePhase(2);
            ((BadWolf) owner).rollMove();
            ((BadWolf) owner).createIntent();
        }
    }

    @Override
    public void onRemove() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                owner.halfDead = false;
                this.isDone = true;
            }
        });
        if (owner instanceof BadWolf) {
            ((BadWolf) owner).changePhase(1);
        }
    }

    @Override
    public void atEndOfRound() {
        if (amount == 1) {
            makePowerRemovable(owner, ID);
        }
        atb(new ReducePowerAction(owner, owner, this, 1));
    }

    @Override
    public void updateDescription() {
        if (amount == 1) {
            description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[2];
        } else {
            description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
        }
    }
}
