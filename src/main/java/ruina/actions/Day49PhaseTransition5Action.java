package ruina.actions;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.monsters.day49.Act5Angela;
import ruina.monsters.day49.dialogue.Day49PhaseTransition4;
import ruina.monsters.day49.dialogue.Day49PhaseTransition5;
import ruina.patches.PostProcessorPatch;
import ruina.shaders.SilentGirl.SilentGirlPostProcessor;
import ruina.shaders.SilentGirl.SilentGirlShader;

public class Day49PhaseTransition5Action extends AbstractGameAction {
    boolean started = false;
    Day49PhaseTransition5 dialogue;
    Act5Angela parent;
    public Day49PhaseTransition5Action(int start, int end, Act5Angela source) {
        this.actionType = ActionType.SPECIAL;
        dialogue = new Day49PhaseTransition5(start, end, source);
    }

    @Override
    public void update() {
        if (!started) {
            CardCrawlGame.fadeIn(2.5f);
            CustomDungeon.playTempMusicInstantly("WindBGM");
            AbstractDungeon.topLevelEffectsQueue.add(dialogue);
            started = true;
        }
        if (dialogue.isDone) {
            this.isDone = true;
        }
    }
}