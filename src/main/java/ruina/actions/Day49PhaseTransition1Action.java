package ruina.actions;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import ruina.monsters.day49.Act2Angela;
import ruina.monsters.day49.Act3Angela;
import ruina.monsters.day49.dialogue.Day49InitialDialogue;
import ruina.monsters.day49.dialogue.Day49PhaseTransition1;

public class Day49PhaseTransition1Action extends AbstractGameAction {
    boolean started = false;
    Day49PhaseTransition1 dialogue;

    public Day49PhaseTransition1Action(int start, int end) {
        this.actionType = ActionType.SPECIAL;
        dialogue = new Day49PhaseTransition1(start, end);
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
            ++AbstractDungeon.bossCount;
            StatsScreen.incrementBossSlain();
            if(AbstractDungeon.bossList.isEmpty()){ AbstractDungeon.bossList.add("Hexaghost"); }
            AbstractDungeon.bossKey = Act2Angela.ID;
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