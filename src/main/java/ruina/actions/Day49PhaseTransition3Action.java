package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import ruina.monsters.day49.Act2Angela;
import ruina.monsters.day49.Act4Angela;
import ruina.monsters.day49.dialogue.Day49PhaseTransition1;
import ruina.monsters.day49.dialogue.Day49PhaseTransition3;

public class Day49PhaseTransition3Action extends AbstractGameAction {
    boolean started = false;
    Day49PhaseTransition3 dialogue;

    public Day49PhaseTransition3Action(int start, int end) {
        this.actionType = ActionType.SPECIAL;
        dialogue = new Day49PhaseTransition3(start, end);
    }

    @Override
    public void update() {
        if (!started) {
            CardCrawlGame.fadeIn(2.5f);
            AbstractDungeon.topLevelEffectsQueue.add(dialogue);
            started = true;
        }
        if (dialogue.isDone) {
            if(AbstractDungeon.bossList.isEmpty()){ AbstractDungeon.bossList.add("Hexaghost"); }
            AbstractDungeon.bossKey = Act4Angela.ID;
            CardCrawlGame.music.fadeOutBGM();
            CardCrawlGame.music.fadeOutTempBGM();
            MapRoomNode node = new MapRoomNode(-1, 15);
            node.room = new MonsterRoomBoss();
            AbstractDungeon.nextRoom = node;
            AbstractDungeon.closeCurrentScreen();
            AbstractDungeon.nextRoomTransitionStart();
            this.isDone = true;
        }
    }
}