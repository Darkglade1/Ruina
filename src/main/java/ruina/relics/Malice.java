package ruina.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class Malice extends AbstractEasyRelic {
    public static final String ID = makeID(Malice.class.getSimpleName());

    private static final int DAMAGE = 2;
    private static final int LOW_HP_DAMAGE = 5;
    private static final int LOW_HP_THRESHOLD = 50;

    public Malice() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void atTurnStart() {
        this.flash();
        atb(new RelicAboveCreatureAction(adp(), this));
        if (adp().currentHealth <= adp().maxHealth * ((float) LOW_HP_THRESHOLD / 100)) {
            atb(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(LOW_HP_DAMAGE, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.POISON));
        } else {
            atb(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(DAMAGE, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.POISON));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + DAMAGE + DESCRIPTIONS[1] + LOW_HP_THRESHOLD + DESCRIPTIONS[2] + LOW_HP_DAMAGE + DESCRIPTIONS[3];
    }
}
