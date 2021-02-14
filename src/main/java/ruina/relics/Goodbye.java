package ruina.relics;

import ruina.powers.GoodbyePower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class Goodbye extends AbstractEasyRelic {
    public static final String ID = makeID(Goodbye.class.getSimpleName());

    public Goodbye() {
        super(ID, RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public void atPreBattle() {
        flash();
        applyToTarget(adp(), adp(), new GoodbyePower(adp()));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
