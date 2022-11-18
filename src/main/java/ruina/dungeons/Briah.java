package ruina.dungeons;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import ruina.RuinaMod;
import ruina.monsters.act2.*;

import java.util.ArrayList;

public class Briah extends AbstractRuinaDungeon {

    public static String ID = RuinaMod.makeID("Briah");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];

    public Briah() {
        super(NAME, ID, "images/ui/event/panel.png", false, 2, 12, 10);
        this.addTempMusic("Warning1", RuinaMod.makeMusicPath("Warning1.ogg"));
        this.addTempMusic("Warning2", RuinaMod.makeMusicPath("Warning2.ogg"));
        this.addTempMusic("Warning3", RuinaMod.makeMusicPath("Warning3.ogg"));
        this.addTempMusic("Roland1", RuinaMod.makeMusicPath("Roland1.ogg"));
        this.addTempMusic("Roland2", RuinaMod.makeMusicPath("Roland2.ogg"));
        this.addTempMusic("Roland3", RuinaMod.makeMusicPath("Roland3.ogg"));
        this.addTempMusic("Samurai", RuinaMod.makeMusicPath("Samurai.ogg"));
    }

    public Briah(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public Briah(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
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
        if (CardCrawlGame.dungeon instanceof Asiyah) {
            return TEXT[2];
        } else {
            String[] oldStrings = CardCrawlGame.languagePack.getUIString(Asiyah.ID).TEXT;
            return oldStrings[2];
        }

    }

    @Override
    public String getOptionText() {
        if (CardCrawlGame.dungeon instanceof Asiyah) {
            return TEXT[3];
        } else {
            String[] oldStrings = CardCrawlGame.languagePack.getUIString(Asiyah.ID).TEXT;
            return oldStrings[3];
        }
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
        //monsters.add(new MonsterInfo("Spheric Guardian", 2.0F));
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
        //monsters.add(new MonsterInfo("Cultist and Chosen", 3.0F));
        monsters.add(new MonsterInfo(EncounterIDs.SCARECROWS_3, 3.0F));
        //monsters.add(new MonsterInfo("Shelled Parasite and Fungi", 3.0F));
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
}