package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.monsters.day49.dialogue.Day49EndingDialogue;

public class Day49EndingDialogueAction extends AbstractGameAction {
    boolean started = false;
    Day49EndingDialogue dialogue;

    public Day49EndingDialogueAction(int start, int end) {
        this.actionType = ActionType.SPECIAL;
        dialogue = new Day49EndingDialogue(start, end);
    }

    @Override
    public void update() {
        if (!started) {
            CardCrawlGame.fadeIn(2f);
            AbstractDungeon.topLevelEffectsQueue.add(dialogue);
            started = true;
        }
        if (dialogue.isDone) {
            this.isDone = true;
        }
    }
}