package ruina.powers.act1;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.cards.performance.*;
import ruina.powers.AbstractUnremovablePower;

import java.util.ArrayList;

import static ruina.util.Wiz.intoDiscard;

public class Performance extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Performance.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final ArrayList<AbstractCard> performerCards = new ArrayList<>();

    public Performance(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        performerCards.add(new FirstChair());
        performerCards.add(new SecondChair());
        performerCards.add(new ThirdChair());
        performerCards.add(new FourthChair());
        if (AbstractDungeon.ascensionLevel >= 19) {
            for (AbstractCard card : performerCards) {
                card.upgrade();
            }
        }
        updateDescription();
    }

    @Override
    public void atEndOfRound() {
        if (!performerCards.isEmpty()) {
            flash();
            intoDiscard(performerCards.remove(0), 1);
        }
        updateDescription();
    }

    @Override
    public void onExhaust(AbstractCard card) {
        if (card instanceof AbstractPerformanceCard) {
            flash();
            intoDiscard(card.makeStatEquivalentCopy(), 1);
        }
    }

    @Override
    public void updateDescription() {
        if (performerCards != null) {
            if (!performerCards.isEmpty()) {
                description = POWER_DESCRIPTIONS[0] + " " + POWER_DESCRIPTIONS[1];
            } else {
                description = POWER_DESCRIPTIONS[1];
            }
            if (AbstractDungeon.ascensionLevel >= 19) {
                description += POWER_DESCRIPTIONS[2];
            }
        }
    }
}
