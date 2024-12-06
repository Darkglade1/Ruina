package ruina.powers.act5;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import java.util.HashMap;
import java.util.Map;

public class SingularityJ extends AbstractUnremovablePower implements OnReceivePowerPower {
    public static final String POWER_ID = RuinaMod.makeID(SingularityJ.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final int LIMIT = 3;
    private static final int UNLOCKED_LIMIT = 1;
    private HashMap<AbstractCreature, Integer> attackLimitMap = new HashMap<>();
    private HashMap<AbstractCreature, Integer> debuffLimitMap = new HashMap<>();
    private boolean justStrengthDown = false;
    private boolean negateGainStrengthUp = false;

    public SingularityJ(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, LIMIT);
        this.name = DESCRIPTIONS[3];
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (power.type == PowerType.DEBUFF) {
            if (debuffLimitMap.containsKey(source)) {
                int times = debuffLimitMap.get(source);

                //Handle temp Strength down effects
                if (power instanceof StrengthPower) {
                    if (times < this.amount) {
                        justStrengthDown = true;
                    } else {
                        negateGainStrengthUp = true;
                    }
                }
                if (power instanceof GainStrengthPower) {
                    if (justStrengthDown) {
                        justStrengthDown = false;
                        return true;
                    }
                    if (negateGainStrengthUp) {
                        negateGainStrengthUp = false;
                        return false;
                    }
                }

                if (times >= this.amount) {
                    return false;
                } else {
                    times++;
                    debuffLimitMap.put(source, times);
                    return true;
                }
            } else {
                debuffLimitMap.put(source, 1);
                return true;
            }
        }
        return true;
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL) {
            AbstractCreature source = info.owner;
            if (attackLimitMap.containsKey(source)) {
                int times = attackLimitMap.get(source);
                if (times >= this.amount) {
                    return 0;
                } else {
                    times++;
                    attackLimitMap.put(source, times);
                    return damageAmount;
                }
            } else {
                attackLimitMap.put(source, 1);
                return damageAmount;
            }
        }
        return damageAmount;
    }

    @Override
    public void atStartOfTurn() {
        for (Map.Entry<AbstractCreature,Integer> entry : debuffLimitMap.entrySet()) {
            entry.setValue(0);
        }
        for (Map.Entry<AbstractCreature,Integer> entry : attackLimitMap.entrySet()) {
            entry.setValue(0);
        }
    }

    public void unlock() {
        this.amount = UNLOCKED_LIMIT;
        this.name = NAME;
        updateDescription();
        flash();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1] + amount + DESCRIPTIONS[2];
    }
}