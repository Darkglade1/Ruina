package ruina.powers;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.day49.Act1Angela;
import ruina.monsters.day49.Act2Angela;
import ruina.monsters.day49.Act4Angela;

import java.text.DecimalFormat;

public class RighteousHammerPiercingNail extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(RighteousHammerPiercingNail.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public RighteousHammerPiercingNail(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        loadRegion("combust");
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

}