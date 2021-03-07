package ruina.dungeons;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import ruina.RuinaMod;
import ruina.monsters.act3.Bloodbath;
import ruina.monsters.act3.BurrowingHeaven;
import ruina.monsters.act3.JudgementBird;
import ruina.monsters.act3.Pinocchio;
import ruina.monsters.act3.SnowQueen.SnowQueen;
import ruina.monsters.act3.bigBird.BigBird;
import ruina.monsters.act3.blueStar.BlueStar;
import ruina.monsters.act3.heart.HeartOfAspiration;
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
        this.addTempMusic("RedMistBGM", RuinaMod.makeMusicPath("RedMistBgm.ogg"));
        this.addTempMusic("Gebura2", RuinaMod.makeMusicPath("Gebura2.ogg"));
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
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(HeartOfAspiration.ID, 2.0F));
        monsters.add(new MonsterInfo(Bloodbath.ID, 2.0F));
        monsters.add(new MonsterInfo(EncounterIDs.BIRDS_3, 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }

    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(BurrowingHeaven.ID, 1.0F));
        //monsters.add(new MonsterInfo("Transient", 1.0F));
        monsters.add(new MonsterInfo(EncounterIDs.BIRDS_4, 1.0F));
        monsters.add(new MonsterInfo(JudgementBird.ID, 1.0F));
        monsters.add(new MonsterInfo(PunishingBird.ID, 1.0F));
        monsters.add(new MonsterInfo(PriceOfSilence.ID, 1.0F));
        monsters.add(new MonsterInfo(Pinocchio.ID, 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }

    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(BigBird.ID, 2.0F));
        monsters.add(new MonsterInfo(SnowQueen.ID, 2.0F));
        monsters.add(new MonsterInfo(BlueStar.ID, 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, true);
    }

    @Override
    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
        String previous = monsterList.get(monsterList.size() - 1);
        if (previous.equals(EncounterIDs.BIRDS_3)) {
            retVal.add(EncounterIDs.BIRDS_4);
        }
        return retVal;
    }
}