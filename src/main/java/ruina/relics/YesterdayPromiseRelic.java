package ruina.relics;

import static ruina.RuinaMod.makeID;

public class YesterdayPromiseRelic extends AbstractEasyRelic {
    public static final String ID = makeID(YesterdayPromiseRelic.class.getSimpleName());

    public YesterdayPromiseRelic() {
        super(ID, RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}