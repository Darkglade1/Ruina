package ruina.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class Strongest extends AbstractEasyRelic {
    public static final String ID = makeID(Strongest.class.getSimpleName());

    private static final int DAMAGE_THRESHOLD = 20;
    private static final int STRENGTH = 1;

    public Strongest() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void atTurnStart() {
        counter = 0;
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL) {
            counter += damageAmount;
            int strengthToGain = (counter / DAMAGE_THRESHOLD) * STRENGTH;
            counter = counter % DAMAGE_THRESHOLD;
            if (strengthToGain > 0) {
                flash();
                atb(new RelicAboveCreatureAction(adp(), this));
                applyToTarget(adp(), adp(), new StrengthPower(adp(), strengthToGain));
            }
        }
    }

    @Override
    public void onVictory() {
        counter = -1;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + DAMAGE_THRESHOLD + DESCRIPTIONS[1] + STRENGTH + DESCRIPTIONS[2];
    }
}
