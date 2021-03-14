package ruina.actions;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashIntentEffect;

public class BetterIntentFlashAction extends AbstractGameAction {
    private final AbstractMonster m;
    private final Texture intentImg;

    public BetterIntentFlashAction(AbstractMonster m, Texture intentImg) {
        if (Settings.FAST_MODE) {
            this.startDuration = Settings.ACTION_DUR_MED;
        } else {
            this.startDuration = Settings.ACTION_DUR_XLONG;
        }

        this.duration = this.startDuration;
        this.m = m;
        this.intentImg = intentImg;
        this.actionType = ActionType.WAIT;
    }

    @Override
    public void update() {
        if (m != null && m.isDeadOrEscaped()) {
            isDone = true;
        } else {
            if (this.duration == this.startDuration) {
                if (this.intentImg != null && m != null) {
                    AbstractDungeon.effectList.add(new FlashIntentEffect(this.intentImg, m));
                    m.intentAlphaTarget = 0.0F;
                }
            }
            this.tickDuration();
        }
    }
}
