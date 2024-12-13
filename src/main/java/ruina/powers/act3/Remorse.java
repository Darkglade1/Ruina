package ruina.powers.act3;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;

import static ruina.util.Wiz.makeInHand;

public class Remorse extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(Remorse.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final AbstractCard curse;

    public Remorse(AbstractCreature owner, int amount, AbstractCard curse) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.curse = curse;
        updateDescription();
    }

    @Override
    public void atEndOfRound() {
        makeInHand(curse.makeStatEquivalentCopy(), amount);
    }

    @Override
    public void updateDescription() {
        if (curse != null) {
            description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + FontHelper.colorString(curse.name, "y") + POWER_DESCRIPTIONS[2];
        }
    }
}
