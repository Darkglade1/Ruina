package ruina.actions;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import ruina.monsters.day49.Act5Angela;
import ruina.monsters.day49.FinalAngela;
import ruina.monsters.day49.dialogue.Day49PhaseTransition5;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.TreeOfLifeManager;

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
            ++AbstractDungeon.bossCount;
            StatsScreen.incrementBossSlain();
            if(AbstractDungeon.bossList.isEmpty()){ AbstractDungeon.bossList.add("Hexaghost"); }
            AbstractDungeon.bossKey = FinalAngela.ID;
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