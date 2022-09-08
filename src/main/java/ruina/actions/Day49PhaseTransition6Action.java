package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.monsters.day49.dialogue.Day49PhaseTransition4;
import ruina.monsters.day49.dialogue.Day49PhaseTransition6;

public class Day49PhaseTransition6Action extends AbstractGameAction {
    boolean started = false;
    Day49PhaseTransition6 dialogue;

    public Day49PhaseTransition6Action(int start, int end) {
        this.actionType = ActionType.SPECIAL;
        dialogue = new Day49PhaseTransition6(start, end);
    }

    @Override
    public void update() {
        if (!started) {
            //CardCrawlGame.fadeIn(0.5f);
            AbstractDungeon.topLevelEffectsQueue.add(dialogue);
            started = true;
        }
        if (dialogue.isDone) {
            this.isDone = true;
        }
    }
}