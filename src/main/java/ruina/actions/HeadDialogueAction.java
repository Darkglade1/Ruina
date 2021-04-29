package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.monsters.theHead.dialogue.HeadDialogue;

public class HeadDialogueAction extends AbstractGameAction {
    boolean started = false;
    HeadDialogue dialogue;

    public HeadDialogueAction(int start, int end) {
        this.actionType = ActionType.SPECIAL;
        dialogue = new HeadDialogue(start, end);
    }

    @Override
    public void update() {
        if (!started) {
            AbstractDungeon.topLevelEffectsQueue.add(dialogue);
            started = true;
        }
        if (dialogue.isDone) {
            this.isDone = true;
        }
    }
}