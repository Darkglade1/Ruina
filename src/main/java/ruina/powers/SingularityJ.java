package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.RuinaMod;

import java.util.HashMap;
import java.util.Map;

public class SingularityJ extends AbstractUnremovablePower implements OnReceivePowerPower {
    public static final String POWER_ID = RuinaMod.makeID(SingularityJ.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final int LIMIT = 3;
    private static final int UNLOCKED_LIMIT = 1;
    private HashMap<AbstractCreature, Integer> limitMap = new HashMap<>();

    public SingularityJ(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, LIMIT);
        this.name = DESCRIPTIONS[3];
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (power.type == PowerType.DEBUFF) {
            if (limitMap.containsKey(source)) {
                int times = limitMap.get(source);
                if (times >= this.amount) {
                    return false;
                } else {
                    times++;
                    limitMap.put(source, times);
                    return true;
                }
            } else {
                limitMap.put(source, 1);
                return true;
            }
        }
        return true;
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        AbstractCreature source = info.owner;
        if (limitMap.containsKey(source)) {
            int times = limitMap.get(source);
            if (times >= this.amount) {
                return 0;
            } else {
                times++;
                limitMap.put(source, times);
                return damageAmount;
            }
        } else {
            limitMap.put(source, 1);
            return damageAmount;
        }
    }

    @Override
    public void atStartOfTurn() {
        for (Map.Entry<AbstractCreature,Integer> entry : limitMap.entrySet()) {
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