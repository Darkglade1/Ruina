package ruina.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTargetTop;

public class Hate extends AbstractEasyRelic {
    public static final String ID = makeID(Hate.class.getSimpleName());
    private static final int STR = 1;

    public Hate() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void wasHPLost(int damageAmount) {
        if (damageAmount > 0 && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            this.flash();
            this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            applyToTargetTop(adp(), adp(), new StrengthPower(adp(), STR));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + STR + DESCRIPTIONS[1];
    }
}
