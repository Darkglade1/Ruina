package ruina.dungeons;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.rooms.TrueVictoryRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import ruina.RuinaMod;
import ruina.events.act4.Ensemble;
import ruina.monsters.uninvitedGuests.bremen.Bremen;
import ruina.monsters.uninvitedGuests.clown.Oswald;
import ruina.monsters.uninvitedGuests.elena.Elena;
import ruina.monsters.uninvitedGuests.greta.Greta;
import ruina.monsters.uninvitedGuests.philip.Philip;
import ruina.monsters.uninvitedGuests.pluto.monster.Pluto;
import ruina.monsters.uninvitedGuests.puppeteer.Puppeteer;
import ruina.monsters.uninvitedGuests.tanya.Tanya;
import ruina.rooms.ReverbMonsterRoom;

import java.util.ArrayList;

import static ruina.RuinaMod.makeUIPath;

public class UninvitedGuests extends AbstractRuinaDungeon {

    public static String ID = RuinaMod.makeID(UninvitedGuests.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];

    public UninvitedGuests() {
        super(NAME, ID, "images/ui/event/panel.png", false, 2, 12, 10);
        this.onEnterEvent(Ensemble.class);
        this.addTempMusic("Ensemble1", RuinaMod.makeMusicPath("Reverberation1st_Asiyah.ogg"));
        this.addTempMusic("Ensemble2", RuinaMod.makeMusicPath("Reverberation1st_Briah.ogg"));
        this.addTempMusic("Ensemble3", RuinaMod.makeMusicPath("Reverberation1st_Atziluth.ogg"));
        this.addTempMusic("EnsembleArgalia", RuinaMod.makeMusicPath("Reverberation1st_Argalia.ogg"));
    }

    public UninvitedGuests(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public UninvitedGuests(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
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
    public String getBodyText() {
        return TEXT[2];
    }

    @Override
    public String getOptionText() {
        return TEXT[3];
    }

    @Override
    protected void makeMap() {
        System.out.println("ho");
        ArrayList<MonsterRoomCreator> row1 = new ArrayList();
        ArrayList<MonsterRoomCreator> row2 = new ArrayList();
        ArrayList<MonsterRoomCreator> row3 = new ArrayList();
        ArrayList<MonsterRoomCreator> row4 = new ArrayList();
        ArrayList<MonsterRoomCreator> row5 = new ArrayList();
        ArrayList<MonsterRoomCreator> row6 = new ArrayList();
        ArrayList<MonsterRoomCreator> row7 = new ArrayList();
        ArrayList<MonsterRoomCreator> row8 = new ArrayList();
        ArrayList<MonsterRoomCreator> row9 = new ArrayList();

        row1.add(new MonsterRoomCreator(makeUIPath("MalkuthMap.png"), makeUIPath("MalkuthMapOutline.png"), Philip.ID));
        row2.add(new MonsterRoomCreator(makeUIPath("YesodMap.png"), makeUIPath("YesodMapOutline.png"), EncounterIDs.RED_AND_WOLF));
        row3.add(new MonsterRoomCreator(makeUIPath("HodMap.png"), makeUIPath("HodMapOutline.png"), Greta.ID));
        row4.add(new MonsterRoomCreator(makeUIPath("NetzachMap.png"), makeUIPath("NetzachMapOutline.png"), Bremen.ID));
        row5.add(new MonsterRoomCreator(makeUIPath("TiphMap.png"), makeUIPath("TiphMapOutline.png"), Oswald.ID));
        row6.add(new MonsterRoomCreator(makeUIPath("GeburaMap.png"), makeUIPath("GeburaMapOutline.png"), Tanya.ID));
        row7.add(new MonsterRoomCreator(makeUIPath("ChesedMap.png"), makeUIPath("ChesedMapOutline.png"), Puppeteer.ID));
        row8.add(new MonsterRoomCreator(makeUIPath("BinahMap.png"), makeUIPath("BinahMapOutline.png"), Elena.ID));
        row9.add(new MonsterRoomCreator(makeUIPath("HokmaMap.png"), makeUIPath("HokmaMapOutline.png"), Pluto.ID));

        map = new ArrayList();

        int index = 0;
        map.add(populate(row1, index++));
        //map.add(populate(row2, index++));
        map.add(populate(row3, index++));
        map.add(doubleNodeArea(new TreasureRoom(), new RestRoom(), index++));
        map.add(doubleNodeArea(new TreasureRoom(), new RestRoom(), index++));
        map.add(populate(row4, index++));
        map.add(populate(row5, index++));
        map.add(populate(row6, index++));
        map.add(doubleNodeArea(new TreasureRoom(), new RestRoom(), index++));
        map.add(doubleNodeArea(new TreasureRoom(), new RestRoom(), index++));
        map.add(populate(row7, index++));
        map.add(populate(row8, index++));
        //map.add(populate(row9, index++));
        map.add(tripleNodeArea(new TreasureRoom(), new ShopRoom(), new RestRoom(), index++));
        map.add(tripleNodeArea(new TreasureRoom(), new ShopRoom(), new RestRoom(), index++));
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

        public MonsterRoom get() { return new ReverbMonsterRoom(encounterID, image, outline); }
    }

}