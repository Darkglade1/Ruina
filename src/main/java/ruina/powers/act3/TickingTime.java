package ruina.powers.act3;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.TimeWarpTurnEndEffect;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;

import static ruina.monsters.AbstractRuinaMonster.playSound;

public class TickingTime extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(TickingTime.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final int CARD_PLAY_THRESHOLD;

    public TickingTime(AbstractCreature owner, int amount, int cardPlayThreshold) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.CARD_PLAY_THRESHOLD = cardPlayThreshold;
        updateDescription();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.flashWithoutSound();
        this.amount++;
        if (this.amount == CARD_PLAY_THRESHOLD) {
            this.amount = 0;
            AbstractDungeon.actionManager.callEndTurnEarlySequence();
            playSound("SilenceStop");
            AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.GOLD, true));
            AbstractDungeon.topLevelEffectsQueue.add(new TimeWarpTurnEndEffect());
        }
        this.updateDescription();
    }

    //this power gets reset in the halfdead allies patch so it works with take extra turn shit LOL

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + CARD_PLAY_THRESHOLD + POWER_DESCRIPTIONS[1];
    }
}
