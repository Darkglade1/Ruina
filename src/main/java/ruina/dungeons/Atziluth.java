package ruina.dungeons;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import ruina.RuinaMod;
import ruina.monsters.act2.BadWolf;
import ruina.monsters.act2.KingOfGreed;
import ruina.monsters.act2.KnightOfDespair;
import ruina.monsters.act2.Mountain;
import ruina.monsters.act2.Nosferatu;
import ruina.monsters.act2.QueenOfHate;
import ruina.monsters.act2.RoadHome;
import ruina.monsters.act2.ServantOfWrath;
import ruina.monsters.act2.Woodsman;
import ruina.monsters.act3.Bloodbath;
import ruina.monsters.act3.BurrowingHeaven;
import ruina.monsters.act3.bigBird.BigBird;
import ruina.monsters.act3.blueStar.BlueStar;
import ruina.monsters.act3.priceOfSilence.PriceOfSilence;
import ruina.monsters.act3.punishingBird.PunishingBird;

import java.util.ArrayList;

public class Atziluth extends AbstractRuinaDungeon {

    public static String ID = RuinaMod.makeID("Atziluth");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];

    public Atziluth() {
        super(NAME, ID, "images/ui/event/panel.png", false, 2, 12, 10);
        this.addTempMusic("Angela1", RuinaMod.makeMusicPath("Angela1.ogg"));
        this.addTempMusic("Angela2", RuinaMod.makeMusicPath("Angela2.ogg"));
        this.addTempMusic("Angela3", RuinaMod.makeMusicPath("Angela3.ogg"));
        this.addTempMusic("WhiteNightBGM", RuinaMod.makeMusicPath("WhiteNightBGM.ogg"));
    }

    public Atziluth(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public Atziluth(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
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
    protected void generateMonsters() {
        generateWeakEnemies(weakpreset);
        generateStrongEnemies(strongpreset);
        generateElites(elitepreset);
    }

    protected void generateWeakEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();
        monsters.add(new MonsterInfo("3 Darklings", 2.0F));
        monsters.add(new MonsterInfo(Bloodbath.ID, 2.0F));
        monsters.add(new MonsterInfo("3 Shapes", 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }

    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();
        monsters.add(new MonsterInfo(BurrowingHeaven.ID, 1.0F));
        monsters.add(new MonsterInfo("Transient", 1.0F));
        monsters.add(new MonsterInfo("4 Shapes", 1.0F));
        monsters.add(new MonsterInfo("Maw", 1.0F));
        monsters.add(new MonsterInfo(PunishingBird.ID, 1.0F));
        monsters.add(new MonsterInfo("Jaw Worm Horde", 1.0F));
        monsters.add(new MonsterInfo(PriceOfSilence.ID, 1.0F));
        monsters.add(new MonsterInfo("Writhing Mass", 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }

    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();
        monsters.add(new MonsterInfo(BigBird.ID, 2.0F));
        monsters.add(new MonsterInfo("Nemesis", 2.0F));
        monsters.add(new MonsterInfo(BlueStar.ID, 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, true);
    }

    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList();
        String var2 = (String)monsterList.get(monsterList.size() - 1);
        byte var3 = -1;
        switch(var2.hashCode()) {
            case -500373089:
                if (var2.equals("3 Shapes")) {
                    var3 = 2;
                }
                break;
            case 1014856122:
                if (var2.equals("3 Darklings")) {
                    var3 = 0;
                }
                break;
            case 1679632599:
                if (var2.equals("Orb Walker")) {
                    var3 = 1;
                }
        }

        switch(var3) {
            case 0:
                retVal.add("3 Darklings");
                break;
            case 1:
                retVal.add("Orb Walker");
                break;
            case 2:
                retVal.add("4 Shapes");
        }

        return retVal;
    }
}