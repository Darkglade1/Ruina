package ruina.dungeons;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import ruina.RuinaMod;
import ruina.scenes.RuinaScene;

import java.util.ArrayList;
import java.util.Collections;

public class Briah extends CustomDungeon {

    public static String ID = RuinaMod.makeID("Briah");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];

    public Briah() {
        super(NAME, ID, "images/ui/event/panel.png", true, 2, 12, 10);
//        this.setMainMusic("audio/music/Gensokyo/KidFestival.ogg");
//        this.addTempMusic("FateOfSixtyYears", "audio/music/Gensokyo/FateOfSixtyYears.ogg");
    }

    public Briah(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public Briah(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
        super(cd, p, saveFile);
    }

    @Override
    public AbstractScene DungeonScene() {
        return new RuinaScene();
    }

    protected void initializeLevelSpecificChances() {
        shopRoomChance = 0.05F;
        restRoomChance = 0.12F;
        treasureRoomChance = 0.0F;
        eventRoomChance = 0.25F;
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
        monsters.add(new MonsterInfo("Spheric Guardian", 2.0F));
        monsters.add(new MonsterInfo("Chosen", 2.0F));
        monsters.add(new MonsterInfo("Shell Parasite", 2.0F));
        monsters.add(new MonsterInfo("3 Byrds", 2.0F));
        monsters.add(new MonsterInfo("2 Thieves", 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }

    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();
        monsters.add(new MonsterInfo("Chosen and Byrds", 2.0F));
        monsters.add(new MonsterInfo("Sentry and Sphere", 2.0F));
        monsters.add(new MonsterInfo("Snake Plant", 6.0F));
        monsters.add(new MonsterInfo("Snecko", 4.0F));
        monsters.add(new MonsterInfo("Centurion and Healer", 6.0F));
        monsters.add(new MonsterInfo("Cultist and Chosen", 3.0F));
        monsters.add(new MonsterInfo("3 Cultists", 3.0F));
        monsters.add(new MonsterInfo("Shelled Parasite and Fungi", 3.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }

    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();
        monsters.add(new MonsterInfo("Gremlin Leader", 1.0F));
        monsters.add(new MonsterInfo("Slavers", 1.0F));
        monsters.add(new MonsterInfo("Book of Stabbing", 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, true);
    }

    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList();
        String var2 = (String)monsterList.get(monsterList.size() - 1);
        byte var3 = -1;
        switch(var2.hashCode()) {
            case -1001149827:
                if (var2.equals("3 Byrds")) {
                    var3 = 1;
                }
                break;
            case 1989842815:
                if (var2.equals("Spheric Guardian")) {
                    var3 = 0;
                }
                break;
            case 2017619858:
                if (var2.equals("Chosen")) {
                    var3 = 2;
                }
        }

        switch(var3) {
            case 0:
                retVal.add("Sentry and Sphere");
                break;
            case 1:
                retVal.add("Chosen and Byrds");
                break;
            case 2:
                retVal.add("Chosen and Byrds");
                retVal.add("Cultist and Chosen");
        }

        return retVal;
    }

    @Override
    protected void initializeShrineList() {
    }

    @Override
    protected void initializeEventList() {
        // Events are added via BaseMod in GensokyoMod.receivePostInitialize()
    }
}