package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.monsters.day49.dialogue.Day49InitialDialogue;

public class Day49InitialDialogueAction extends AbstractGameAction {
    boolean started = false;
    Day49InitialDialogue dialogue;

    public Day49InitialDialogueAction(int start, int end) {
        this.actionType = ActionType.SPECIAL;
        dialogue = new Day49InitialDialogue(start, end);
    }

    @Override
    public void update() {
        if (!started) {
            CardCrawlGame.fadeIn(1.5f);
            AbstractDungeon.topLevelEffectsQueue.add(dialogue);
            started = true;
        }
        if (dialogue.isDone) {
            this.isDone = true;
        }
    }
}