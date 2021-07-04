package ruina.events.act1;

import basemod.CustomEventRoom;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import ruina.RuinaMod;

import java.util.ArrayList;

import static ruina.RuinaMod.makeEventPath;

public class UnknownPath extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(UnknownPath.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("UnknownPath.png");

    private int screenNum = 0;

    public UnknownPath() {
        super(NAME, DESCRIPTIONS[0], IMG);
        noCardsInRewards = true;
        imageEventText.setDialogOption(OPTIONS[0]);
        imageEventText.setDialogOption(OPTIONS[1]);
        imageEventText.setDialogOption(OPTIONS[2]);

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                screenNum = 1;
                switch (buttonPressed) {
                    case 0:
                        encounterEliteEnemy();
                        break;
                    case 1:
                        encounterNormalEnemy();
                        break;
                    case 2:
                        encounterRandomEvent();
                        break;
                }
                break;
            default:
                openMap();
                break;
        }
    }

    private void encounterNormalEnemy() {
        CardCrawlGame.music.unsilenceBGM();
        final MapRoomNode cur = AbstractDungeon.currMapNode;
        final MapRoomNode node = new MapRoomNode(cur.x, cur.y);
        node.room = new MonsterRoom();
        final ArrayList<MapEdge> curEdges = cur.getEdges();
        for (final MapEdge edge : curEdges) {
            node.addEdge(edge);
        }
        AbstractDungeon.previousScreen = null;
        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.dungeonMapScreen.closeInstantly();
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.fadeIn();
        AbstractDungeon.dungeonMapScreen.dismissable = true;
        AbstractDungeon.setCurrMapNode(AbstractDungeon.nextRoom = node);
        AbstractDungeon.getCurrRoom().onPlayerEntry();
        AbstractDungeon.player.preBattlePrep();
        AbstractDungeon.scene.nextRoom(node.room);
        AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
        AbstractDungeon.monsterList.remove(0);

        cur.taken = true;
    }

    private void encounterEliteEnemy() {
        CardCrawlGame.music.unsilenceBGM();
        final MapRoomNode cur2 = AbstractDungeon.currMapNode;
        final MapRoomNode node2 = new MapRoomNode(cur2.x, cur2.y);
        node2.room = new MonsterRoomElite();
        final ArrayList<MapEdge> curEdges2 = cur2.getEdges();
        for (final MapEdge edge : curEdges2) {
            node2.addEdge(edge);
        }
        AbstractDungeon.previousScreen = null;
        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.dungeonMapScreen.closeInstantly();
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.fadeIn();
        AbstractDungeon.dungeonMapScreen.dismissable = true;
        AbstractDungeon.setCurrMapNode(AbstractDungeon.nextRoom = node2);
        AbstractDungeon.getCurrRoom().onPlayerEntry();
        AbstractDungeon.player.preBattlePrep();
        AbstractDungeon.scene.nextRoom(node2.room);
        AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
        System.out.println(AbstractDungeon.eliteMonsterList);
        AbstractDungeon.eliteMonsterList.remove(0);
        System.out.println(AbstractDungeon.eliteMonsterList);
        cur2.taken = true;
    }

    private void encounterRandomEvent() {
        ArrayList<String> possibleEvents = new ArrayList<>(AbstractDungeon.eventList);
        String evt = possibleEvents.get(AbstractDungeon.eventRng.random(possibleEvents.size() - 1));
        AbstractDungeon.eventList.remove(evt);
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.eventList.add(0, evt);
        final MapRoomNode cur3 = AbstractDungeon.currMapNode;
        final MapRoomNode node3 = new MapRoomNode(cur3.x, cur3.y);
        node3.room = new CustomEventRoom();
        final ArrayList<MapEdge> curEdges3 = cur3.getEdges();
        for (final MapEdge edge : curEdges3) {
            node3.addEdge(edge);
        }
        AbstractDungeon.previousScreen = null;
        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.dungeonMapScreen.closeInstantly();
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.fadeIn();
        AbstractDungeon.dungeonMapScreen.dismissable = true;
        AbstractDungeon.setCurrMapNode(AbstractDungeon.nextRoom = node3);
        AbstractDungeon.getCurrRoom().onPlayerEntry();
        AbstractDungeon.scene.nextRoom(node3.room);
        AbstractDungeon.rs = AbstractDungeon.RenderScene.EVENT;

        cur3.taken = true;
    }

}