package ruina.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.monsters.theHead.Zena;

import static ruina.util.Wiz.*;

public class AnArbiter extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(AnArbiter.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public AnArbiter(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.priority = 10;
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && info.owner == owner && info.type == DamageInfo.DamageType.NORMAL) {
            AbstractCreature enemy = target;
            int powerAmt = this.amount;
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    int debuff = powerAmt;
                    AbstractPower str = enemy.getPower(StrengthPower.POWER_ID);
                    if (str != null) {
                        if (str.amount > 0) {
                            if (debuff > str.amount) {
                                debuff = str.amount;
                            }
                            applyToTargetTop(enemy, owner, new StrengthPower(enemy, -debuff));
                        }
                    }
                    this.isDone = true;
                }
            });
            applyToTargetNextTurn(owner, new StrengthPower(owner, amount));
        }
    }

    @Override
    public void atEndOfRound() {
        if (owner instanceof Zena) {
            ((Zena) owner).enrage();
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}