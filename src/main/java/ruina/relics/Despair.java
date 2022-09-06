package ruina.relics;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

public class Despair extends AbstractEasyRelic {
    public static final String ID = makeID(Despair.class.getSimpleName());
    private static final int BLOCK = 6;
    private boolean triggered = false;

    public Despair() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
        this.pulse = false;
    }

    @Override
    public void onExhaust(AbstractCard card) {
        if (!triggered) {
            AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(adp(), adp(), BLOCK));
            this.pulse = false;
            triggered = true;
        }
    }

    @Override
    public void atTurnStart() {
        this.beginPulse();
        this.pulse = true;
        triggered = false;
    }

    @Override
    public void onVictory() {
        triggered = false;
        this.pulse = false;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + BLOCK + DESCRIPTIONS[1];
    }
}
