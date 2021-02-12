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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import ruina.CustomIntent.MassAttackIntent;
import ruina.cards.AbstractRuinaCard;
import ruina.cards.cardvars.SecondDamage;
import ruina.cards.cardvars.SecondMagicNumber;
import ruina.dungeons.Briah;
import ruina.dungeons.EncounterIDs;
import ruina.events.act2.ChurchOfGears;
import ruina.events.act2.Messenger;
import ruina.events.act2.RCorp;
import ruina.events.act2.SocialSciences;
import ruina.events.act2.ThePianist;
import ruina.events.act2.WizardOfOz;
import ruina.events.act2.ZweiAssociation;
import ruina.monsters.act2.BadWolf;
import ruina.monsters.act2.Hermit;
import ruina.monsters.act2.Jester.JesterOfNihil;
import ruina.monsters.act2.KingOfGreed;
import ruina.monsters.act2.KnightOfDespair;
import ruina.monsters.act2.LittleRed;
import ruina.monsters.act2.Mountain;
import ruina.monsters.act2.NightmareWolf;
import ruina.monsters.act2.Nosferatu;
import ruina.monsters.act2.Ozma;
import ruina.monsters.act2.QueenOfHate;
import ruina.monsters.act2.RoadHome;
import ruina.monsters.act2.SanguineBat;
import ruina.monsters.act2.Scarecrow;
import ruina.monsters.act2.ServantOfWrath;
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

    public static class Enums {
        @SpireEnum(name = "EGO") // These two HAVE to have the same absolutely identical name.
        public static AbstractCard.CardColor EGO;
        @SpireEnum(name = "EGO") @SuppressWarnings("unused")
        public static CardLibrary.LibraryType LIBRARY_COLOR;
    }

    public static Color EGO_COLOR = CardHelper.getColor(76, 7, 23);
    private static final String ATTACK_S_ART = getModID() + "Resources/images/512/card.png";
    private static final String SKILL_S_ART = getModID() + "Resources/images/512/card.png";
    private static final String POWER_S_ART = getModID() + "Resources/images/512/card.png";
    private static final String CARD_ENERGY_S = getModID() + "Resources/images/512/energy.png";
    private static final String TEXT_ENERGY = getModID() + "Resources/images/512/text_energy.png";
    private static final String ATTACK_L_ART = getModID() + "Resources/images/1024/card.png";
    private static final String SKILL_L_ART = getModID() + "Resources/images/1024/card.png";
    private static final String POWER_L_ART = getModID() + "Resources/images/1024/card.png";
    private static final String CARD_ENERGY_L = getModID() + "Resources/images/1024/energy.png";

    //This is for the in-game mod settings panel.
    private static final String MODNAME = "Ruina";
    private static final String AUTHOR = "Darkglade";
    private static final String DESCRIPTION = "An alternate Act mod inspired by Library of Ruina.";

    // =============== INPUT TEXTURE LOCATION =================

    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "ruinaResources/images/Badge.png";

    public RuinaMod() {
        BaseMod.subscribe(this);

        BaseMod.addColor(Enums.EGO, EGO_COLOR, EGO_COLOR, EGO_COLOR,
                EGO_COLOR, EGO_COLOR, EGO_COLOR, EGO_COLOR,
                ATTACK_S_ART, SKILL_S_ART, POWER_S_ART, CARD_ENERGY_S,
                ATTACK_L_ART, SKILL_L_ART, POWER_L_ART,
                CARD_ENERGY_L, TEXT_ENERGY);
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

    public static String makeEventPath(String resourcePath) {
        return modID + "Resources/images/events/" + resourcePath;
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

        BaseMod.addAudio(makeID("OzmaGuard"), makeSFXPath("Ozma_Guard.wav"));
        BaseMod.addAudio(makeID("OzmaFin"), makeSFXPath("Ozma_StrongAtk_Fin.wav"));
        BaseMod.addAudio(makeID("OzmaStart"), makeSFXPath("Ozma_StrongAtk_Start.wav"));

        BaseMod.addAudio(makeID("WrathMeet"), makeSFXPath("Angry_Meet.wav"));
        BaseMod.addAudio(makeID("HermitAtk"), makeSFXPath("Angry_R_Atk.wav"));
        BaseMod.addAudio(makeID("HermitStrongAtk"), makeSFXPath("Angry_R_StrongAtk.wav"));
        BaseMod.addAudio(makeID("HermitWand"), makeSFXPath("Angry_R_WandHit.wav"));
        BaseMod.addAudio(makeID("WrathStrong1"), makeSFXPath("Angry_StrongAtk1.wav"));
        BaseMod.addAudio(makeID("WrathStrong2"), makeSFXPath("Angry_StrongAtk2.wav"));
        BaseMod.addAudio(makeID("WrathStrong3"), makeSFXPath("Angry_StrongFinish.wav"));
        BaseMod.addAudio(makeID("WrathAtk1"), makeSFXPath("Angry_Vert1.wav"));
        BaseMod.addAudio(makeID("WrathAtk2"), makeSFXPath("Angry_Vert2.wav"));

        BaseMod.addAudio(makeID("BluntBlow"), makeSFXPath("Blow_Stab.wav"));
        BaseMod.addAudio(makeID("IndexUnlock"), makeSFXPath("IndexUnlock.wav"));
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
        BaseMod.addDynamicVariable(new SecondMagicNumber());
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
        BaseMod.addMonster(ServantOfWrath.ID, "Servant_of_Wrath", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new ServantOfWrath(-500.0F, 0.0F),
                        new Hermit(100.0F, 0.0F),
                }));

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
        briah.addBoss(Ozma.ID, (BaseMod.GetMonster) Ozma::new, makeMonsterPath("Ozma/Ozma.png"), makeMonsterPath("Ozma/OzmaOutline.png"));
        briah.addBoss(JesterOfNihil.ID, (BaseMod.GetMonster) JesterOfNihil::new, makeMonsterPath("Jester/JesterMap.png"), makeMonsterPath("Jester/JesterMapOutline.png"));

        BaseMod.addEvent(WizardOfOz.ID, WizardOfOz.class, Briah.ID);
        BaseMod.addEvent(ChurchOfGears.ID, ChurchOfGears.class, Briah.ID);
        BaseMod.addEvent(ZweiAssociation.ID, ZweiAssociation.class, Briah.ID);
        BaseMod.addEvent(RCorp.ID, RCorp.class, Briah.ID);
        BaseMod.addEvent(SocialSciences.ID, SocialSciences.class, Briah.ID);
        BaseMod.addEvent(ThePianist.ID, ThePianist.class, Briah.ID);
        BaseMod.addEvent(Messenger.ID, Messenger.class, Briah.ID);
    }


    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class, getModID() + "Resources/localization/eng/Cardstrings.json");

        BaseMod.loadCustomStringsFile(RelicStrings.class, getModID() + "Resources/localization/eng/Relicstrings.json");

        BaseMod.loadCustomStringsFile(PowerStrings.class, getModID() + "Resources/localization/eng/Powerstrings.json");

        BaseMod.loadCustomStringsFile(MonsterStrings.class, getModID() + "Resources/localization/eng/Monsterstrings.json");

        BaseMod.loadCustomStringsFile(EventStrings.class, getModID() + "Resources/localization/eng/Eventstrings.json");

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
