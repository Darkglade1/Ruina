package ruina.dungeons;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import ruina.RuinaMod;
import ruina.events.NeowAngela;
import ruina.monsters.act1.AllAroundHelper;
import ruina.monsters.act1.Alriune;
import ruina.monsters.act1.ForsakenMurderer;
import ruina.monsters.act1.Fragment;
import ruina.monsters.act1.GalaxyFriend;
import ruina.monsters.act1.Porccubus;
import ruina.monsters.act1.ShyLook;
import ruina.monsters.act1.TeddyBear;
import ruina.monsters.act1.laetitia.Laetitia;
import ruina.monsters.act1.queenBee.QueenBee;
import ruina.monsters.act1.scorchedGirl.ScorchedGirl;
import ruina.monsters.act1.spiderBud.SpiderBud;

import java.util.ArrayList;

public class Asiyah extends AbstractRuinaDungeon {

    public static String ID = RuinaMod.makeID("Asiyah");
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];

    public Asiyah() {
        super(NAME, ID, "images/ui/event/panel.png", false, 3, 12, 10);
        this.onEnterEvent(NeowAngela.class);
    }

    public Asiyah(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public Asiyah(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
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
        cardUpgradedChance = 0.0F;
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
        monsters.add(new MonsterInfo(Fragment.ID, 2.0F));
        monsters.add(new MonsterInfo(ForsakenMurderer.ID, 2.0F));
        monsters.add(new MonsterInfo(EncounterIDs.EMPLOYEES_2, 2.0F));
        monsters.add(new MonsterInfo(EncounterIDs.BUTTERFLIES_3, 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(TeddyBear.ID, 2.0F));
        monsters.add(new MonsterInfo(SpiderBud.ID, 2.0F));
        monsters.add(new MonsterInfo(Porccubus.ID, 2.0F));
        monsters.add(new MonsterInfo(ShyLook.ID, 2.0F));
        monsters.add(new MonsterInfo(EncounterIDs.BUTTERFLIES_5, 1.0F));
        monsters.add(new MonsterInfo(QueenBee.ID, 2.0F));
        monsters.add(new MonsterInfo(GalaxyFriend.ID, 2.0F));
        monsters.add(new MonsterInfo(ScorchedGirl.ID, 2.0F));
        monsters.add(new MonsterInfo(EncounterIDs.EMPLOYEES_3, 1.0F));
        monsters.add(new MonsterInfo(EncounterIDs.RED_SHOES, 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(Laetitia.ID, 1.0F));
        monsters.add(new MonsterInfo(Alriune.ID, 1.0F));
        monsters.add(new MonsterInfo(AllAroundHelper.ID, 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, true);
    }

    @Override
    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
        String previous = monsterList.get(monsterList.size() - 1);
        if (previous.equals(EncounterIDs.EMPLOYEES_2)) {
            retVal.add(EncounterIDs.EMPLOYEES_3);
        }
        if (previous.equals(EncounterIDs.BUTTERFLIES_3)) {
            retVal.add(EncounterIDs.BUTTERFLIES_5);
        }
        return retVal;
    }
}