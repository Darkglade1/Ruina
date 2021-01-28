package ruina;

import actlikeit.RazIntent.CustomIntent;
import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.helpers.RelicType;
import basemod.interfaces.AddAudioSubscriber;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.events.city.Addict;
import com.megacrit.cardcrawl.events.city.BackToBasics;
import com.megacrit.cardcrawl.events.city.Beggar;
import com.megacrit.cardcrawl.events.city.DrugDealer;
import com.megacrit.cardcrawl.events.city.ForgottenAltar;
import com.megacrit.cardcrawl.events.city.Nest;
import com.megacrit.cardcrawl.events.city.TheLibrary;
import com.megacrit.cardcrawl.events.city.TheMausoleum;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import ruina.CustomIntent.MassAttackIntent;
import ruina.cards.AbstractRuinaCard;
import ruina.cards.cardvars.SecondDamage;
import ruina.cards.cardvars.SillyVariable;
import ruina.dungeons.Briah;
import ruina.dungeons.EncounterIDs;
import ruina.monsters.act2.BadWolf;
import ruina.monsters.act2.Jester.JesterOfNihil;
import ruina.monsters.act2.KingOfGreed;
import ruina.monsters.act2.KnightOfDespair;
import ruina.monsters.act2.LittleRed;
import ruina.monsters.act2.Mountain;
import ruina.monsters.act2.NightmareWolf;
import ruina.monsters.act2.Nosferatu;
import ruina.monsters.act2.QueenOfHate;
import ruina.monsters.act2.RoadHome;
import ruina.monsters.act2.SanguineBat;
import ruina.monsters.act2.Scarecrow;
import ruina.monsters.act2.Woodsman;
import ruina.relics.AbstractEasyRelic;
import ruina.util.TexLoader;

import java.nio.charset.StandardCharsets;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class RuinaMod implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        PostInitializeSubscriber,
        AddAudioSubscriber {

    private static final String modID = "ruina";
    public static String getModID() {
        return modID;
    }
    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }

//    public static Color todoColor = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);
//    public static final String SHOULDER1 = getModID() + "Resources/images/char/mainChar/shoulder.png";
//    public static final String SHOULDER2 = getModID() + "Resources/images/char/mainChar/shoulder2.png";
//    public static final String CORPSE = getModID() + "Resources/images/char/mainChar/corpse.png";
//    private static final String ATTACK_S_ART = getModID() + "Resources/images/512/attack.png";
//    private static final String SKILL_S_ART = getModID() + "Resources/images/512/skill.png";
//    private static final String POWER_S_ART = getModID() + "Resources/images/512/power.png";
//    private static final String CARD_ENERGY_S = getModID() + "Resources/images/512/energy.png";
//    private static final String TEXT_ENERGY = getModID() + "Resources/images/512/text_energy.png";
//    private static final String ATTACK_L_ART = getModID() + "Resources/images/1024/attack.png";
//    private static final String SKILL_L_ART = getModID() + "Resources/images/1024/skill.png";
//    private static final String POWER_L_ART = getModID() + "Resources/images/1024/power.png";
//    private static final String CARD_ENERGY_L = getModID() + "Resources/images/1024/energy.png";
//    private static final String CHARSELECT_BUTTON = getModID() + "Resources/images/charSelect/charButton.png";
//    private static final String CHARSELECT_PORTRAIT = getModID() + "Resources/images/charSelect/charBG.png";

    //This is for the in-game mod settings panel.
    private static final String MODNAME = "Ruina";
    private static final String AUTHOR = "Darkglade";
    private static final String DESCRIPTION = "An alternate Act mod inspired by Library of Ruina.";

    // =============== INPUT TEXTURE LOCATION =================

    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "ruinaResources/images/Badge.png";

    public RuinaMod() {
        BaseMod.subscribe(this);

//        BaseMod.addColor(TheTodo.Enums.TODO_COLOR, todoColor, todoColor, todoColor,
//                todoColor, todoColor, todoColor, todoColor,
//                ATTACK_S_ART, SKILL_S_ART, POWER_S_ART, CARD_ENERGY_S,
//                ATTACK_L_ART, SKILL_L_ART, POWER_L_ART,
//                CARD_ENERGY_L, TEXT_ENERGY);
    }

    public static String makePath(String resourcePath) {
        return modID + "Resources/" + resourcePath;
    }

    public static String makeImagePath(String resourcePath) {
        return modID + "Resources/images/" + resourcePath;
    }

    public static String makeRelicPath(String resourcePath) {
        return modID + "Resources/images/relics/" + resourcePath;
    }

    public static String makePowerPath(String resourcePath) {
        return modID + "Resources/images/powers/" + resourcePath;
    }

    public static String makeCardPath(String resourcePath) {
        return modID + "Resources/images/cards/" + resourcePath;
    }

    public static String makeSFXPath(String resourcePath) {
        return modID + "Resources/audio/sfx/" + resourcePath;
    }

    public static String makeMusicPath(String resourcePath) {
        return modID + "Resources/audio/music/" + resourcePath;
    }

    public static String makeMonsterPath(String resourcePath) {
        return modID + "Resources/images/monsters/" + resourcePath;
    }

    public static String makeUIPath(String resourcePath) {
        return modID + "Resources/images/ui/" + resourcePath;
    }

    public static void initialize() {
        RuinaMod ruinaMod = new RuinaMod();
    }

    @Override
    public void receiveAddAudio() {
        BaseMod.addAudio(makeID("Bite"), makeSFXPath("Wolf_Bite.wav"));
        BaseMod.addAudio(makeID("Claw"), makeSFXPath("Wolf_Hori.wav"));
        BaseMod.addAudio(makeID("Howl"), makeSFXPath("Wolf_Howl.wav"));
        BaseMod.addAudio(makeID("Fog"), makeSFXPath("Wolf_FogChange.wav"));
        BaseMod.addAudio(makeID("WolfPhase"), makeSFXPath("Wolf_Phase2.wav"));

        BaseMod.addAudio(makeID("Slash"), makeSFXPath("RedHood_Atk1.wav"));
        BaseMod.addAudio(makeID("Gun"), makeSFXPath("RedHood_Gun.wav"));
        BaseMod.addAudio(makeID("Rage"), makeSFXPath("RedHood_Rage.wav"));

        BaseMod.addAudio(makeID("Ram"), makeSFXPath("Danggo_Lv3_Atk.wav"));
        BaseMod.addAudio(makeID("Screech"), makeSFXPath("Danggo_Lv2_Shout.wav"));
        BaseMod.addAudio(makeID("Vomit"), makeSFXPath("Danggo_Lv3_Special.wav"));
        BaseMod.addAudio(makeID("Grow"), makeSFXPath("Danggo_LvUp.wav"));
        BaseMod.addAudio(makeID("Shrink"), makeSFXPath("Danggo_LvDown.wav"));
        BaseMod.addAudio(makeID("Spawn"), makeSFXPath("Danggo_Birth.wav"));

        BaseMod.addAudio(makeID("Rake"), makeSFXPath("Scarecrow_Atk2.wav"));
        BaseMod.addAudio(makeID("Harvest"), makeSFXPath("Scarecrow_Drink.wav"));
        BaseMod.addAudio(makeID("ScarecrowDeath"), makeSFXPath("Scarecrow_Dead.wav"));

        BaseMod.addAudio(makeID("WoodStrike"), makeSFXPath("WoodMachine_AtkStrong.wav"));
        BaseMod.addAudio(makeID("WoodFinish"), makeSFXPath("WoodMachine_Kill.wav"));

        BaseMod.addAudio(makeID("MagicAttack"), makeSFXPath("MagicalGirl_Atk.wav"));
        BaseMod.addAudio(makeID("MagicKiss"), makeSFXPath("MagicalGirl_kiss.wav"));
        BaseMod.addAudio(makeID("MagicGun"), makeSFXPath("MagicalGirl_Gun.wav"));
        BaseMod.addAudio(makeID("MagicSnakeAtk"), makeSFXPath("MagicalGirl_SnakeAtk.wav"));
        BaseMod.addAudio(makeID("MagicSnakeGun"), makeSFXPath("MagicalGirl_SnakeAtk_gun.wav"));

        BaseMod.addAudio(makeID("GreedGetPower"), makeSFXPath("Greed_GetPower.wav"));
        BaseMod.addAudio(makeID("GreedBlunt"), makeSFXPath("Greed_Stab.wav"));
        BaseMod.addAudio(makeID("GreedSlam"), makeSFXPath("Greed_StrongAtk.wav"));
        BaseMod.addAudio(makeID("GreedDiamond"), makeSFXPath("Greed_MakeDiamond.wav"));
        BaseMod.addAudio(makeID("GreedStabChange"), makeSFXPath("Greed_Stab_Change.wav"));
        BaseMod.addAudio(makeID("GreedStrAtkChange"), makeSFXPath("Greed_StrongAtk_Change.wav"));
        BaseMod.addAudio(makeID("GreedVertChange"), makeSFXPath("Greed_Vert_Change.wav"));
        BaseMod.addAudio(makeID("GreedStrAtkReady"), makeSFXPath("Greed_StrongAtk_Ready.wav"));

        BaseMod.addAudio(makeID("KnightAttack"), makeSFXPath("KnightOfDespair_Atk_Strong.wav"));
        BaseMod.addAudio(makeID("KnightChange"), makeSFXPath("KnightOfDespair_Change.wav"));
        BaseMod.addAudio(makeID("KnightGaho"), makeSFXPath("KnightOfDespair_Gaho.wav"));
        BaseMod.addAudio(makeID("KnightVertGaho"), makeSFXPath("KnightOfDespair_Vert_gaho.wav"));

        BaseMod.addAudio(makeID("BatAttack"), makeSFXPath("Nosferatu_Atk_Bat.wav"));
        BaseMod.addAudio(makeID("NosChange"), makeSFXPath("Nosferatu_Change.wav"));
        BaseMod.addAudio(makeID("NosBloodEat"), makeSFXPath("Nosferatu_Changed_BloodEat.wav"));
        BaseMod.addAudio(makeID("NosGrab"), makeSFXPath("Nosferatu_Changed_Grab.wav"));
        BaseMod.addAudio(makeID("NosSpecial"), makeSFXPath("Nosferatu_Changed_StrongAtk_Start.wav"));

        BaseMod.addAudio(makeID("HouseBoom"), makeSFXPath("House_HouseBoom.wav"));
        BaseMod.addAudio(makeID("LionPoison"), makeSFXPath("House_Lion_Poison.wav"));
        BaseMod.addAudio(makeID("MakeRoad"), makeSFXPath("House_MakeRoad.wav"));
        BaseMod.addAudio(makeID("HouseAttack"), makeSFXPath("House_NormalAtk.wav"));
        BaseMod.addAudio(makeID("LionChange"), makeSFXPath("House_Lion_Change.wav"));
    }

    @Override
    public void receiveEditRelics() {
        new AutoAdd(modID)
                .packageFilter(AbstractEasyRelic.class)
                .any(AbstractEasyRelic.class, (info, relic) -> {
                    if (relic.color == null) {
                        BaseMod.addRelic(relic, RelicType.SHARED);
                    } else {
                        BaseMod.addRelicToCustomPool(relic, relic.color);
                    }
                });
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addDynamicVariable(new SillyVariable());
        BaseMod.addDynamicVariable(new SecondDamage());
        new AutoAdd(modID)
                .packageFilter(AbstractRuinaCard.class)
                .setDefaultSeen(false)
                .cards();
    }

    @Override
    public void receivePostInitialize() {
        // Load the Mod Badge
        Texture badgeTexture = TexLoader.getTexture(BADGE_IMAGE);

        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();

        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        Briah briah = new Briah();
        briah.addAct(TheCity.ID);

        CustomIntent.add(new MassAttackIntent());

        BaseMod.addMonster(Mountain.ID, (BaseMod.GetMonster) Mountain::new);
        BaseMod.addMonster(RoadHome.ID, (BaseMod.GetMonster) RoadHome::new);

        BaseMod.addMonster(EncounterIDs.SCARECROWS_2, "2_Scarecrows", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new Scarecrow(-450.0F, 0.0F, 1),
                        new Scarecrow(-150.0F, 0.0F, 0),
                }));
        BaseMod.addMonster(EncounterIDs.SCARECROWS_3, "3_Scarecrowss", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new Scarecrow(-450.0F, 0.0F, 2),
                        new Scarecrow(-200.0F, 0.0F, 1),
                        new Scarecrow(50.0F, 0.0F, 0)
                }));
        BaseMod.addMonster(Woodsman.ID, (BaseMod.GetMonster) Woodsman::new);
        BaseMod.addMonster(EncounterIDs.BATS_3, "3_Bats", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new SanguineBat(-450.0F, 0.0F),
                        new SanguineBat(-200.0F, 0.0F),
                        new SanguineBat(50.0F, 0.0F)
                }));
        BaseMod.addMonster(Nosferatu.ID, (BaseMod.GetMonster) Nosferatu::new);
        BaseMod.addMonster(EncounterIDs.NOS_AND_BAT, "Nos_and_Bat", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new SanguineBat(-450.0F, 0.0F),
                        new Nosferatu(-150.0F, 0.0F),
                }));
        BaseMod.addMonster(KnightOfDespair.ID, (BaseMod.GetMonster) KnightOfDespair::new);
        BaseMod.addMonster(KingOfGreed.ID, (BaseMod.GetMonster) KingOfGreed::new);
        BaseMod.addMonster(BadWolf.ID, (BaseMod.GetMonster) BadWolf::new);
        BaseMod.addMonster(QueenOfHate.ID, (BaseMod.GetMonster) QueenOfHate::new);

        briah.addBoss(EncounterIDs.RED_AND_WOLF, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new LittleRed(-480.0F, 0.0F),
                        new NightmareWolf(),
                }), makeMonsterPath("LittleRed/Red.png"), makeMonsterPath("LittleRed/RedOutline.png"));
        briah.addBoss(JesterOfNihil.ID, (BaseMod.GetMonster) JesterOfNihil::new, makeMonsterPath("Jester/JesterMap.png"), makeMonsterPath("Jester/JesterMapOutline.png"));


        BaseMod.addEvent(Addict.ID, Addict.class, Briah.ID);
        BaseMod.addEvent(BackToBasics.ID, BackToBasics.class, Briah.ID);
        BaseMod.addEvent(Beggar.ID, Beggar.class, Briah.ID);
        BaseMod.addEvent(DrugDealer.ID, DrugDealer.class, Briah.ID);
        BaseMod.addEvent(Nest.ID, Nest.class, Briah.ID);
        BaseMod.addEvent(TheLibrary.ID, TheLibrary.class, Briah.ID);
        BaseMod.addEvent(TheMausoleum.ID, TheMausoleum.class, Briah.ID);
        BaseMod.addEvent(ForgottenAltar.ID, ForgottenAltar.class, Briah.ID);

    }


    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class, getModID() + "Resources/localization/eng/Cardstrings.json");

        BaseMod.loadCustomStringsFile(RelicStrings.class, getModID() + "Resources/localization/eng/Relicstrings.json");

        BaseMod.loadCustomStringsFile(PowerStrings.class, getModID() + "Resources/localization/eng/Powerstrings.json");

        BaseMod.loadCustomStringsFile(MonsterStrings.class, getModID() + "Resources/localization/eng/Monsterstrings.json");

        BaseMod.loadCustomStringsFile(UIStrings.class, getModID() + "Resources/localization/eng/UIstrings.json");
    }

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String json = Gdx.files.internal(getModID() + "Resources/localization/eng/Keywordstrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(modID, keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }
}
