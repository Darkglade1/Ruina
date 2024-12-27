package ruina.powers.multiplayer;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

public class MultiplayerEnemyBuff extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(MultiplayerEnemyBuff.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public MultiplayerEnemyBuff(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        this.amount = (RuinaMod.getMultiplayerEnemyHealthScaling(100) - 100);
        updateDescription();
        setPowerImage("MultiplayerAllyBuff");
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
    }
}
