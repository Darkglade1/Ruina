package ruina.monsters.eventBoss.core.power;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.monsters.AbstractRuinaCardMonster;
import ruina.monsters.eventBoss.core.actions.util.EnemyDrawCardAction;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class EnemyDrawPower extends AbstractPower {
    public static final String POWER_ID = makeID(EnemyDrawPower.class.getSimpleName());
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private static final PowerStrings powerStrings;

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Draw");
        NAME = EnemyDrawPower.powerStrings.NAME;
        DESCRIPTIONS = EnemyDrawPower.powerStrings.DESCRIPTIONS;
    }

    public EnemyDrawPower(final AbstractCreature owner, final int drawAmount) {
        this.name = EnemyDrawPower.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = drawAmount;
        this.updateDescription();
        this.loadRegion("draw");
        this.priority = 20;
    }

    @Override
    public void updateDescription() {
        if (this.amount > 0) {
            if (this.amount == 1) { this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
            } else { this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[3]; }
            this.type = PowerType.BUFF;
        } else {
            if (this.amount == -1) { this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[2];
            } else { this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[4]; }
            this.type = PowerType.DEBUFF;
        }

    }

    @Override
    public void atStartOfTurnPostDraw() {
        this.flash();
        addToBot(new EnemyDrawCardAction((AbstractRuinaCardMonster) this.owner, this.amount));
        atb(new ReducePowerAction(this.owner, this.owner, this, 1));
    }
}