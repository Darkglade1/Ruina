package ruina.powers.act4;

import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.makeInHand;

public class FreshMeat extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(FreshMeat.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public final AbstractCard card;

    public FreshMeat(AbstractCreature owner, int amount, AbstractCard card) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.card = card;
        updateDescription();
    }

    @Override
    public void onSpecificTrigger() {
        makeInHand(card);
    }

    @Override
    public void updateDescription() {
        if (card != null) {
            String cardName = FontHelper.colorString(card.name, "y");
            description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + cardName + POWER_DESCRIPTIONS[2] + cardName + POWER_DESCRIPTIONS[3];
        }
    }
}
