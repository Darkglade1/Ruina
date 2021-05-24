package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.SpeechBubble;

public class BetterTalkAction extends AbstractGameAction {
    private String msg;
    private boolean used;
    private float bubbleDuration;
    private boolean faceRight;

    public BetterTalkAction(AbstractCreature source, String text, float duration, float bubbleDuration, boolean faceRight) {
        this.used = false;// 11
        this.faceRight = faceRight;// 13
        this.setValues(source, source);// 16
        if (Settings.FAST_MODE) {// 17
            this.duration = Settings.ACTION_DUR_MED;// 18
        } else {
            this.duration = duration;// 20
        }

        this.msg = text;// 23
        this.actionType = ActionType.TEXT;// 24
        this.bubbleDuration = bubbleDuration;// 25
    }// 27

    public BetterTalkAction(AbstractCreature source, String text) {
        this(source, text, 2.0F, 2.0F, false);// 30
    }// 31

    public BetterTalkAction(AbstractCreature source, String text, boolean faceRight) {
        this(source, text, 2.0F, 2.0F, faceRight);// 30
    }

    public void update() {
        if (!this.used) {// 40
            if (faceRight) {
                AbstractDungeon.effectList.add(new SpeechBubble(this.source.hb.cX + this.source.dialogX + (50.0f * Settings.scale), this.source.hb.cY + this.source.dialogY, this.bubbleDuration, this.msg, faceRight));
            } else {
                AbstractDungeon.effectList.add(new SpeechBubble(this.source.hb.cX + this.source.dialogX, this.source.hb.cY + this.source.dialogY, this.bubbleDuration, this.msg, faceRight));
            }
            this.used = true;// 58
        }
        this.tickDuration();// 61
    }// 62
}
