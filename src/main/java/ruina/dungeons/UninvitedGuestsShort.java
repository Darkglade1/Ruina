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
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.rooms.TrueVictoryRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import ruina.RuinaMod;
import ruina.events.act4.BlackSilenceFork;
import ruina.events.act4.Ensemble;
import ruina.monsters.uninvitedGuests.normal.bremen.Bremen;
import ruina.monsters.uninvitedGuests.normal.clown.Oswald;
import ruina.monsters.uninvitedGuests.normal.eileen.Eileen;
import ruina.monsters.uninvitedGuests.normal.elena.Elena;
import ruina.monsters.uninvitedGuests.normal.greta.Greta;
import ruina.monsters.uninvitedGuests.normal.philip.Philip;
import ruina.monsters.uninvitedGuests.normal.pluto.monster.Pluto;
import ruina.monsters.uninvitedGuests.normal.puppeteer.Puppeteer;
import ruina.monsters.uninvitedGuests.normal.tanya.Tanya;
import ruina.rooms.RuinaMonsterRoom;

import java.util.ArrayList;

import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.adp;

public class UninvitedGuestsShort extends AbstractRuinaDungeon {

    public static String ID = RuinaMod.makeID(UninvitedGuests.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];

    public static String REAL_ID = RuinaMod.makeID(UninvitedGuestsShort.class.getSimpleName());

    public UninvitedGuestsShort() {
        super(NAME, REAL_ID, "images/ui/event/panel.png", false, 2, 12, 10);
        this.onEnterEvent(Ensemble.class);
    }

    public UninvitedGuestsShort(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public UninvitedGuestsShort(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
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
        AbstractDungeon.rs = RenderScene.EVENT;

        AbstractDungeon.combatRewardScreen.clear();
        AbstractDungeon.previousScreen = null;
        AbstractDungeon.closeCurrentScreen();
    }

    public static class ForkEventRoom extends EventRoom {
        @Override
        public void onPlayerEntry() {
            AbstractDungeon.overlayMenu.proceedButton.hide();
            this.event = new BlackSilenceFork();
            this.event.onEnterRoom();
        }
    }

    @Override
    public String getBodyText() {
        return "";
    }

    @Override
    public String getOptionText() {
        return TEXT[3] + TEXT[5];
    }

    @Override
    protected void makeMap() {
        ArrayList<MonsterRoomCreator> row1 = new ArrayList();
        ArrayList<MonsterRoomCreator> row2 = new ArrayList();
        ArrayList<MonsterRoomCreator> row3 = new ArrayList();

        row1.add(new MonsterRoomCreator(makeUIPath("MalkuthMap.png"), makeUIPath("MalkuthMapOutline.png"), Philip.ID));
        row1.add(new MonsterRoomCreator(makeUIPath("YesodMap.png"), makeUIPath("YesodMapOutline.png"), Eileen.ID));
        row1.add(new MonsterRoomCreator(makeUIPath("HodMap.png"), makeUIPath("HodMapOutline.png"), Greta.ID));
        row2.add(new MonsterRoomCreator(makeUIPath("NetzachMap.png"), makeUIPath("NetzachMapOutline.png"), Bremen.ID));
        row2.add(new MonsterRoomCreator(makeUIPath("TiphMap.png"), makeUIPath("TiphMapOutline.png"), Oswald.ID));
        row2.add(new MonsterRoomCreator(makeUIPath("GeburaMap.png"), makeUIPath("GeburaMapOutline.png"), Tanya.ID));
        row3.add(new MonsterRoomCreator(makeUIPath("ChesedMap.png"), makeUIPath("ChesedMapOutline.png"), Puppeteer.ID));
        row3.add(new MonsterRoomCreator(makeUIPath("BinahMap.png"), makeUIPath("BinahMapOutline.png"), Elena.ID));
        row3.add(new MonsterRoomCreator(makeUIPath("HokmaMap.png"), makeUIPath("HokmaMapOutline.png"), Pluto.ID));

        map = new ArrayList();

        int index = 0;
        map.add(populate(row1, index++));
        map.add(doubleNodeArea(new TreasureRoom(), new RestRoom(), index++));
        map.add(populate(row2, index++));
        map.add(doubleNodeArea(new TreasureRoom(), new RestRoom(), index++));
        map.add(populate(row3, index++));
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
            if (i % 2 == 1) {
                mrn.room = possibilities.get(0).get();
                possibilities.remove(0);
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

    protected ArrayList<MapRoomNode> tripleNodeArea(AbstractRoom roomOne, AbstractRoom roomTwo, AbstractRoom roomThree, int index) {
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