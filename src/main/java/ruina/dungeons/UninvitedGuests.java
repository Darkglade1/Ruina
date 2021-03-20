package ruina.dungeons;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.monsters.ending.CorruptHeart;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import ruina.RuinaMod;
import ruina.monsters.act2.*;

import java.util.ArrayList;

public class UninvitedGuests extends AbstractRuinaDungeon {

    public static String ID = RuinaMod.makeID(UninvitedGuests.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];

    public UninvitedGuests() {
        super(NAME, ID, "images/ui/event/panel.png", false, 2, 12, 10);
        this.addTempMusic("Ensemble1", RuinaMod.makeMusicPath("Reverberation1st_Asiyah.ogg"));
        this.addTempMusic("Ensemble2", RuinaMod.makeMusicPath("Reverberation1st_Briah.ogg"));
        this.addTempMusic("Ensemble3", RuinaMod.makeMusicPath("Reverberation1st_Atziluth.ogg"));
        this.addTempMusic("Ensemble4", RuinaMod.makeMusicPath("Reverberation1st_Argalia.ogg"));
    }

    public UninvitedGuests(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public UninvitedGuests(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
        super(cd, p, saveFile);
    }

    @Override
    protected  void makeMap() {
        long startTime = System.currentTimeMillis();
        map = new ArrayList<>();
        ArrayList<MapRoomNode> row1 = new ArrayList<>();
        MapRoomNode chestRoom = new MapRoomNode(3, 0);
        chestRoom.room = new TreasureRoom();
        MapRoomNode shopRoom = new MapRoomNode(3, 1);
        shopRoom.room = new ShopRoom();
        MapRoomNode restRoom = new MapRoomNode(3, 2);
        restRoom.room = new RestRoom();
        MapRoomNode bossNode = new MapRoomNode(3, 3);
        bossNode.room = new MonsterRoomBoss();
        MapRoomNode victoryNode = new MapRoomNode(3, 4);
        victoryNode.room = new TrueVictoryRoom();
        connectNode(chestRoom, shopRoom);
        connectNode(shopRoom, restRoom);
        restRoom.addEdge(new MapEdge(restRoom.x, restRoom.y, restRoom.offsetX, restRoom.offsetY, bossNode.x, bossNode.y, bossNode.offsetX, bossNode.offsetY, false));
        row1.add(new MapRoomNode(0, 0));
        row1.add(new MapRoomNode(1, 0));
        row1.add(new MapRoomNode(2, 0));
        row1.add(chestRoom);
        row1.add(new MapRoomNode(4, 0));
        row1.add(new MapRoomNode(5, 0));
        row1.add(new MapRoomNode(6, 0));
        ArrayList<MapRoomNode> row2 = new ArrayList<>();
        row2.add(new MapRoomNode(0, 1));
        row2.add(new MapRoomNode(1, 1));
        row2.add(new MapRoomNode(2, 1));
        row2.add(shopRoom);
        row2.add(new MapRoomNode(4, 1));
        row2.add(new MapRoomNode(5, 1));
        row2.add(new MapRoomNode(6, 1));
        ArrayList<MapRoomNode> row3 = new ArrayList<>();
        row3.add(new MapRoomNode(0, 2));
        row3.add(new MapRoomNode(1, 2));
        row3.add(new MapRoomNode(2, 2));
        row3.add(restRoom);
        row3.add(new MapRoomNode(4, 2));
        row3.add(new MapRoomNode(5, 2));
        row3.add(new MapRoomNode(6, 2));
        ArrayList<MapRoomNode> row4 = new ArrayList<>();
        row4.add(new MapRoomNode(0, 3));
        row4.add(new MapRoomNode(1, 3));
        row4.add(new MapRoomNode(2, 3));
        row4.add(bossNode);
        row4.add(new MapRoomNode(4, 3));
        row4.add(new MapRoomNode(5, 3));
        row4.add(new MapRoomNode(6, 3));
        ArrayList<MapRoomNode> row5 = new ArrayList<>();
        row5.add(new MapRoomNode(0, 4));
        row5.add(new MapRoomNode(1, 4));
        row5.add(new MapRoomNode(2, 4));
        row5.add(victoryNode);
        map.add(row1);
        map.add(row2);
        map.add(row3);
        map.add(row4);
        firstRoomChosen = false;
        fadeIn();
    }

    private void connectNode(MapRoomNode src, MapRoomNode dst) { src.addEdge(new MapEdge(src.x, src.y, src.offsetX, src.offsetY, dst.x, dst.y, dst.offsetX, dst.offsetY, false)); }

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

    protected void initializeBoss() {
        bossList.add(CorruptHeart.ID);
        bossList.add(CorruptHeart.ID);
        bossList.add(CorruptHeart.ID);
    }
}