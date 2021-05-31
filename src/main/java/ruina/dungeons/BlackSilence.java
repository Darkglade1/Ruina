package ruina.dungeons;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import ruina.RuinaMod;
import ruina.events.act4.Sorrow;
import ruina.events.act4.TheHead;
import ruina.monsters.blackSilence.blackSilence1.BlackSilence1;
import ruina.monsters.blackSilence.blackSilence3.BlackSilence3;
import ruina.rooms.RuinaMonsterRoom;
import ruina.rooms.RuinaVictoryRoom;

import java.util.ArrayList;

import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.adp;

public class BlackSilence extends AbstractRuinaDungeon {

    public static String ID = RuinaMod.makeID(BlackSilence.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];

    public BlackSilence() {
        super(NAME, ID, "images/ui/event/panel.png", false, 2, 12, 10);
        this.onEnterEvent(Sorrow.class);
        this.addTempMusic("GoneAngels", RuinaMod.makeMusicPath("Gone Angels.ogg"));
        this.addTempMusic("TheHead", RuinaMod.makeMusicPath("TheHead.ogg"));
        this.addTempMusic("Gebura3", RuinaMod.makeMusicPath("Gebura3.ogg"));
        this.addTempMusic("Binah3", RuinaMod.makeMusicPath("Binah3.ogg"));

    }

    public BlackSilence(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public BlackSilence(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
        super(cd, p, saveFile);
    }

    protected void initializeLevelSpecificChances() {
        shopRoomChance = 0.05F;
        restRoomChance = 0.12F;
        treasureRoomChance = 0.0F;
        eventRoomChance = 0.22F;
        eliteRoomChance = 0.08F;
        smallChestChance = 50;
        mediumChestChance = 33;
        largeChestChance = 17;
        commonRelicChance = 50;
        uncommonRelicChance = 33;
        rareRelicChance = 17;
        colorlessRareChance = 0.3F;
        if (AbstractDungeon.ascensionLevel >= 12) {
            cardUpgradedChance = 0.125F;
        } else {
            cardUpgradedChance = 0.25F;
        }
    }

    @Override
    public void Ending() {
        adp().hand.group.clear(); //stop the card playable crashes LOL
        AbstractDungeon.currMapNode.room = new ForkEventRoom();
        AbstractDungeon.getCurrRoom().onPlayerEntry();
        AbstractDungeon.rs = AbstractDungeon.RenderScene.EVENT;

        AbstractDungeon.combatRewardScreen.clear();
        AbstractDungeon.previousScreen = null;
        AbstractDungeon.closeCurrentScreen();
    }

    public static class ForkEventRoom extends EventRoom {
        @Override
        public void onPlayerEntry() {
            AbstractDungeon.overlayMenu.proceedButton.hide();
            this.event = new TheHead();
            this.event.onEnterRoom();
        }

        @Override
        public void endBattle() {
            super.endBattle();
            CardCrawlGame.music.fadeOutBGM();
            MapRoomNode node = new MapRoomNode(3, 4);
            node.room = new RuinaVictoryRoom();
            AbstractDungeon.nextRoom = node;
            AbstractDungeon.closeCurrentScreen();
            AbstractDungeon.nextRoomTransitionStart();
        }
    }


    @Override
    protected void makeMap() {
        ArrayList<MonsterRoomCreator> row1 = new ArrayList();
        ArrayList<MonsterRoomCreator> row2 = new ArrayList();

        row1.add(new MonsterRoomCreator(makeUIPath("KeterMap.png"), makeUIPath("KeterMapOutline.png"), BlackSilence1.ID));
        row2.add(new MonsterRoomCreator(makeUIPath("KeterMap.png"), makeUIPath("KeterMapOutline.png"), BlackSilence3.ID));

        map = new ArrayList();

        int index = 0;
        map.add(populate(row1, index++));
        map.add(singleNodeArea(new RestRoom(), index++));
        map.add(populate(row2, index++));
        map.add(singleNodeArea(new RestRoom(), index++));
        map.add(singleNodeArea(new MonsterRoomBoss(), index++));
        map.add(singleNodeArea(new TrueVictoryRoom(), index++, false));

        logger.info("Generated the following dungeon map:");
        logger.info(MapGenerator.toString(map, true));

        firstRoomChosen = false;
        fadeIn();
    }

    private void connectNode(MapRoomNode src, MapRoomNode dst) {
        src.addEdge(new MapEdge(src.x, src.y, src.offsetX, src.offsetY, dst.x, dst.y, dst.offsetX, dst.offsetY, false));
    }

    private ArrayList<MapRoomNode> populate(ArrayList<MonsterRoomCreator> possibilities, int index) {
        ArrayList<MapRoomNode> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            MapRoomNode mrn = new MapRoomNode(i, index);
            if (i == 3) {
                int rnd = AbstractDungeon.mapRng.random(possibilities.size() - 1);
                mrn.room = possibilities.get(rnd).get();
                possibilities.remove(rnd);
            }
            result.add(mrn);
        }

        if (index > 0) {
            ArrayList<MapRoomNode> mapcontent = map.get(index - 1);
            for (int i = 0; i < mapcontent.size(); i++) {
                if (mapcontent.get(i).room != null) {
                    for (int j = 0; j < result.size(); j++) {
                        if (result.get(j).room != null) {
                            if (Math.abs(i - j) < 4) {
                                this.connectNode(mapcontent.get(i), result.get(j));
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    @Override
    protected void generateMonsters() {
        monsterList = new ArrayList();// 205
        monsterList.add("Shield and Spear");// 206
        monsterList.add("Shield and Spear");// 207
        monsterList.add("Shield and Spear");// 208
        eliteMonsterList = new ArrayList();// 210
        eliteMonsterList.add("Shield and Spear");// 211
        eliteMonsterList.add("Shield and Spear");// 212
        eliteMonsterList.add("Shield and Spear");// 213
    }

    @Override
    protected void generateWeakEnemies(int count) {

    }

    @Override
    protected void generateStrongEnemies(int count) {

    }

    @Override
    protected void generateElites(int count) {

    }

    @Override
    protected ArrayList<String> generateExclusions() {
        return new ArrayList<>();
    }

    private ArrayList<MapRoomNode> tripleNodeArea(AbstractRoom roomOne, AbstractRoom roomTwo, AbstractRoom roomThree, int index) {
        ArrayList<MapRoomNode> result = new ArrayList<>();
        MapRoomNode mrn;
        result.add(new MapRoomNode(0, index));
        mrn = new MapRoomNode(1, index);
        mrn.room = roomOne;
        result.add(mrn);
        result.add(new MapRoomNode(2, index));
        mrn = new MapRoomNode(3, index);
        mrn.room = roomTwo;
        result.add(mrn);
        result.add(new MapRoomNode(4, index));
        mrn = new MapRoomNode(5, index);
        mrn.room = roomThree;
        result.add(mrn);
        result.add(new MapRoomNode(6, index));
        linkNonMonsterAreas(result);
        return result;
    }

    private ArrayList<MapRoomNode> doubleNodeArea(AbstractRoom roomOne, AbstractRoom roomTwo, int index) {
        ArrayList<MapRoomNode> result = new ArrayList<>();
        MapRoomNode mrn;
        result.add(new MapRoomNode(0, index));
        result.add(new MapRoomNode(1, index));
        mrn = new MapRoomNode(2, index);
        mrn.room = roomOne;
        result.add(mrn);
        result.add(new MapRoomNode(3, index));
        mrn = new MapRoomNode(4, index);
        mrn.room = roomTwo;
        result.add(mrn);
        result.add(new MapRoomNode(5, index));
        result.add(new MapRoomNode(6, index));

        linkNonMonsterAreas(result);

        return result;
    }

    private ArrayList<MapRoomNode> singleNodeArea(AbstractRoom room, int index) {
        return singleNodeArea(room, index, true);
    }

    private ArrayList<MapRoomNode> singleNodeArea(AbstractRoom room, int index, boolean connected) {
        ArrayList<MapRoomNode> result = new ArrayList<>();
        MapRoomNode mrn;
        result.add(new MapRoomNode(0, index));
        result.add(new MapRoomNode(1, index));
        result.add(new MapRoomNode(2, index));
        mrn = new MapRoomNode(3, index);
        mrn.room = room;
        result.add(mrn);
        result.add(new MapRoomNode(4, index));
        result.add(new MapRoomNode(5, index));
        result.add(new MapRoomNode(6, index));

        if (connected) {
            linkNonMonsterAreas(result);
        }

        return result;
    }

    private void linkNonMonsterAreas(ArrayList<MapRoomNode> result) {
        if (!map.isEmpty()) {
            ArrayList<MapRoomNode> mapcontent = map.get(map.size() - 1);
            for (int i = 0; i < mapcontent.size(); i++) {
                if (mapcontent.get(i).room != null) {
                    for (int j = 0; j < result.size(); j++) {
                        if (result.get(j).room != null) {
                            this.connectNode(mapcontent.get(i), result.get(j));
                        }
                    }
                }
            }
        }
    }

    static class MonsterRoomCreator {
        String image, outline, encounterID;

        public MonsterRoomCreator(String image, String outline, String encounterID) {
            this.image = image;
            this.outline = outline;
            this.encounterID = encounterID;
        }

        public MonsterRoom get() { return new RuinaMonsterRoom(encounterID, image, outline); }
    }

}