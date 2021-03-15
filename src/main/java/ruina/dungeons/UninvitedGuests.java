package ruina.dungeons;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.monsters.ending.CorruptHeart;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import ruina.RuinaMod;
import ruina.monsters.act2.*;
import ruina.rooms.ReverbMonsterRoom;

import java.util.ArrayList;

public class UninvitedGuests extends AbstractRuinaDungeon {

    public static String ID = RuinaMod.makeID(UninvitedGuests.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];

    public UninvitedGuests() {
        super(NAME, ID, "images/ui/event/panel.png", false, 6, 9, 10);
        this.addTempMusic("Warning1", RuinaMod.makeMusicPath("Warning1.ogg"));
        this.addTempMusic("Warning2", RuinaMod.makeMusicPath("Warning2.ogg"));
        this.addTempMusic("Warning3", RuinaMod.makeMusicPath("Warning3.ogg"));
        this.addTempMusic("Roland1", RuinaMod.makeMusicPath("Roland1.ogg"));
        this.addTempMusic("Roland2", RuinaMod.makeMusicPath("Roland2.ogg"));
        this.addTempMusic("Roland3", RuinaMod.makeMusicPath("Roland3.ogg"));
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

        // TODO: add encounters, add unique mapart.
        row1.add(new MonsterRoomCreator("images/ui/map/monster.png", "images/ui/map/monsterOutline.png", EncounterIDs.RED_AND_WOLF));
        row2.add(new MonsterRoomCreator("images/ui/map/monster.png", "images/ui/map/monsterOutline.png", EncounterIDs.RED_AND_WOLF));
        row3.add(new MonsterRoomCreator("images/ui/map/monster.png", "images/ui/map/monsterOutline.png", EncounterIDs.RED_AND_WOLF));
        row4.add(new MonsterRoomCreator("images/ui/map/monster.png", "images/ui/map/monsterOutline.png", EncounterIDs.RED_AND_WOLF));
        row5.add(new MonsterRoomCreator("images/ui/map/monster.png", "images/ui/map/monsterOutline.png", EncounterIDs.RED_AND_WOLF));
        row6.add(new MonsterRoomCreator("images/ui/map/monster.png", "images/ui/map/monsterOutline.png", EncounterIDs.RED_AND_WOLF));
        row7.add(new MonsterRoomCreator("images/ui/map/monster.png", "images/ui/map/monsterOutline.png", EncounterIDs.RED_AND_WOLF));
        row8.add(new MonsterRoomCreator("images/ui/map/monster.png", "images/ui/map/monsterOutline.png", EncounterIDs.RED_AND_WOLF));
        row9.add(new MonsterRoomCreator("images/ui/map/monster.png", "images/ui/map/monsterOutline.png", EncounterIDs.RED_AND_WOLF));

        map = new ArrayList();

        int index = 0;
        map.add(populate(row1, index++));
        map.add(populate(row2, index++));
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
        map.add(populate(row9, index++));
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
        generateWeakEnemies(weakpreset);
        generateStrongEnemies(strongpreset);
        generateElites(elitepreset);
    }

    @Override
    protected void generateWeakEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(Nosferatu.ID, 2.0F));
        monsters.add(new MonsterInfo(BadWolf.ID, 2.0F));
        monsters.add(new MonsterInfo(EncounterIDs.BATS_3, 2.0F));
        monsters.add(new MonsterInfo(EncounterIDs.SCARECROWS_2, 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(EncounterIDs.NOS_AND_BAT, 2.0F));
        monsters.add(new MonsterInfo(QueenOfHate.ID, 5.0F));
        monsters.add(new MonsterInfo(KnightOfDespair.ID, 6.0F));
        monsters.add(new MonsterInfo(Woodsman.ID, 4.0F));
        monsters.add(new MonsterInfo(KingOfGreed.ID, 6.0F));
        monsters.add(new MonsterInfo(EncounterIDs.SCARECROWS_3, 3.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(ServantOfWrath.ID, 1.0F));
        monsters.add(new MonsterInfo(Mountain.ID, 1.0F));
        monsters.add(new MonsterInfo(RoadHome.ID, 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, true);
    }

    @Override
    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
        String previous = monsterList.get(monsterList.size() - 1);
        if (previous.equals(Nosferatu.ID) || previous.equals(EncounterIDs.BATS_3)) {
            retVal.add(EncounterIDs.NOS_AND_BAT);
        }
        if (previous.equals(EncounterIDs.SCARECROWS_2)) {
            retVal.add(EncounterIDs.SCARECROWS_3);
        }
        return retVal;
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

    class MonsterRoomCreator {
        String image, outline, encounterID;

        public MonsterRoomCreator(String image, String outline, String encounterID) {
            this.image = image;
            this.outline = outline;
            this.encounterID = encounterID;
        }

        public MonsterRoom get() { return new ReverbMonsterRoom(encounterID, image, outline); }
    }

}