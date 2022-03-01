package ruina.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.day49.Act1Angela;

public class Refracting extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Refracting.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Refracting(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        loadRegion("combust");
    }

    @Override
    public void updateDescription() { description =  owner instanceof Act1Angela ? DESCRIPTIONS[0] + DESCRIPTIONS[1] : DESCRIPTIONS[0]; }
}