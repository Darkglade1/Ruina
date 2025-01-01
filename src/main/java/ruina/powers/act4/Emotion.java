package ruina.powers.act4;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.monsters.uninvitedGuests.normal.philip.Malkuth;
import ruina.powers.AbstractEasyPower;

public class Emotion extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(Emotion.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int threshold;
    private final int maxEmotion;

    public Emotion(AbstractCreature owner, int amount, int threshold) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.threshold = threshold;
        this.maxEmotion = Malkuth.EMOTION_TRIGGER_CAP * threshold;
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        if (amount < maxEmotion) {
            this.fontScale = 8.0F;
            this.amount += stackAmount;
            checkTrigger();
        }
    }

    public void checkTrigger() {
        if (amount % threshold == 0) {
            this.flash();
            AbstractPower str = owner.getPower(StrengthPower.POWER_ID);
            if (str != null) {
                owner.addPower(new StrengthPower(owner, str.amount));
                str.flash();
                AbstractDungeon.onModifyPower();
            }
        }
    }

    @Override
    public void onExhaust(AbstractCard card) {
        if (amount < maxEmotion) {
            owner.addPower(new Emotion(owner, Malkuth.EXHAUST_GAIN, threshold));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + threshold + DESCRIPTIONS[1] + Malkuth.EMOTION_TRIGGER_CAP + DESCRIPTIONS[2];
    }
}