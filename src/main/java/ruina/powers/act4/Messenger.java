package ruina.powers.act4;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.cards.AttackPrescript;
import ruina.cards.SkillPrescript;
import ruina.powers.AbstractUnremovablePower;

import java.util.ArrayList;

import static ruina.util.Wiz.makeInHand;

public class Messenger extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Messenger.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Messenger(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
    }
    final ArrayList<AbstractCard> prescripts = new ArrayList<>();

    @Override
    public void atStartOfTurn() {
        if (prescripts.isEmpty()) {
            prescripts.add(new AttackPrescript());
            prescripts.add(new SkillPrescript());
        }
        AbstractCard chosenCard = prescripts.remove(AbstractDungeon.miscRng.random(prescripts.size() - 1));
        makeInHand(chosenCard);
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0];
    }
}
