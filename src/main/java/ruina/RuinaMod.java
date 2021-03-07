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
import basemod.interfaces.PostBattleSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.events.beyond.Falling;
import com.megacrit.cardcrawl.events.beyond.MindBloom;
import com.megacrit.cardcrawl.events.beyond.MysteriousSphere;
import com.megacrit.cardcrawl.events.beyond.SensoryStone;
import com.megacrit.cardcrawl.events.beyond.WindingHalls;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ruina.CustomIntent.MassAttackIntent;
import ruina.cards.AbstractRuinaCard;
import ruina.cards.cardvars.SecondDamage;
import ruina.cards.cardvars.SecondMagicNumber;
import ruina.dungeons.Atziluth;
import ruina.dungeons.Briah;
import ruina.dungeons.EncounterIDs;
import ruina.events.act2.ChildrenOfTheCity;
import ruina.events.act2.ChurchOfGears;
import ruina.events.act2.Language;
import ruina.events.act2.Messenger;
import ruina.events.act2.NothingThere;
import ruina.events.act2.RCorp;
import ruina.events.act2.SocialSciences;
import ruina.events.act2.ThePianist;
import ruina.events.act2.WizardOfOz;
import ruina.events.act2.ZweiAssociation;
import ruina.events.act3.ForThoseWeCherish;
import ruina.events.act3.HanaAssociation;
import ruina.events.act3.Philosophy;
import ruina.events.act3.RedMistRecollection;
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
import ruina.monsters.act3.Bloodbath;
import ruina.monsters.act3.BurrowingHeaven;
import ruina.monsters.act3.EyeballChick;
import ruina.monsters.act3.JudgementBird;
import ruina.monsters.act3.Pinocchio;
import ruina.monsters.eventboss.redMist.monster.RedMist;
import ruina.monsters.act3.RunawayBird;
import ruina.monsters.act3.SnowQueen.SnowQueen;
import ruina.monsters.act3.Twilight;
import ruina.monsters.act3.bigBird.BigBird;
import ruina.monsters.act3.bigBird.Sage;
import ruina.monsters.act3.blueStar.BlueStar;
import ruina.monsters.act3.heart.HeartOfAspiration;
import ruina.monsters.act3.heart.LungsOfCraving;
import ruina.monsters.act3.priceOfSilence.PriceOfSilence;
import ruina.monsters.act3.priceOfSilence.RemnantOfTime;
import ruina.monsters.act3.punishingBird.PunishingBird;
import ruina.monsters.act3.seraphim.Prophet;
import ruina.patches.TotalBlockGainedSpireField;
import ruina.relics.AbstractEasyRelic;
import ruina.util.TexLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static ruina.util.Wiz.adp;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class RuinaMod implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        PostInitializeSubscriber,
        AddAudioSubscriber,
        PostBattleSubscriber {

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

    //This is for the in-EnemyEnergyPanel mod settings panel.
    private static final String MODNAME = "Ruina";
    private static final String AUTHOR = "Darkglade";
    private static final String DESCRIPTION = "An alternate Act mod inspired by Library of Ruina.";

    // =============== INPUT TEXTURE LOCATION =================

    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "ruinaResources/images/Badge.png";

    public static SpireConfig ruinaConfig;
    private static Logger logger = LogManager.getLogger(RuinaMod.class.getName());

    public RuinaMod() {
        BaseMod.subscribe(this);

        BaseMod.addColor(Enums.EGO, EGO_COLOR, EGO_COLOR, EGO_COLOR,
                EGO_COLOR, EGO_COLOR, EGO_COLOR, EGO_COLOR,
                ATTACK_S_ART, SKILL_S_ART, POWER_S_ART, CARD_ENERGY_S,
                ATTACK_L_ART, SKILL_L_ART, POWER_L_ART,
                CARD_ENERGY_L, TEXT_ENERGY);

        Properties ruinaDefaults = new Properties();
        ruinaDefaults.setProperty("Ally Tutorial Seen", "FALSE");
        try {
            ruinaConfig = new SpireConfig("Ruina", "RuinaMod", ruinaDefaults);
        } catch (IOException e) {
            logger.error("RuinaMod SpireConfig initialization failed:");
            e.printStackTrace();
        }
        logger.info("RUINA CONFIG OPTIONS LOADED:");
        logger.info("Ally tutorial seen: " + ruinaConfig.getString("Ally Tutorial Seen") + ".");
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

        BaseMod.addAudio(makeID("ProphetBless"), makeSFXPath("WhiteNight_Bless.wav"));
        BaseMod.addAudio(makeID("ApostleScytheUp"), makeSFXPath("WhiteNight_Apostle_Vert1.wav"));
        BaseMod.addAudio(makeID("ApostleScytheDown"), makeSFXPath("WhiteNight_Apostle_Vert2.wav"));
        BaseMod.addAudio(makeID("ApostleSpear"), makeSFXPath("WhiteNight_Apostle_Spear.wav"));
        BaseMod.addAudio(makeID("ApostleWand"), makeSFXPath("WhiteNight_Apostle_Wand.wav"));
        BaseMod.addAudio(makeID("WhiteNightSummon"), makeSFXPath("WhiteNight_Apostle_Grogy.wav"));
        BaseMod.addAudio(makeID("WhiteNightAppear"), makeSFXPath("WhiteNight_Appear.wav"));
        BaseMod.addAudio(makeID("WhiteNightCall"), makeSFXPath("WhiteNight_Call.wav"));
        BaseMod.addAudio(makeID("WhiteNightCharge"), makeSFXPath("WhiteNight_Strong_Charge.wav"));
        BaseMod.addAudio(makeID("WhiteNightFire"), makeSFXPath("WhiteNight_Strong_Fire.wav"));

        BaseMod.addAudio(makeID("BossBirdLamp"), makeSFXPath("Bossbird_Bigbird_FarAtk.wav"));
        BaseMod.addAudio(makeID("BossBirdBirth"), makeSFXPath("BossBird_Birth.wav"));
        BaseMod.addAudio(makeID("BossBirdCrush"), makeSFXPath("Bossbird_Bossbird_Stab.wav"));
        BaseMod.addAudio(makeID("BossBirdStrong"), makeSFXPath("Bossbird_Bossbird_StrongAtk.wav"));
        BaseMod.addAudio(makeID("BossBirdSlam"), makeSFXPath("Bossbird_Bossbird_VertDown.wav"));
        BaseMod.addAudio(makeID("BossBirdSpecial"), makeSFXPath("Bossbird_Longbird_On.wav"));
        BaseMod.addAudio(makeID("BossBirdPunish"), makeSFXPath("Bossbird_Longbird_StrongAtk.wav"));

        BaseMod.addAudio(makeID("BigBirdLamp"), makeSFXPath("Bigbird_Attract.wav"));
        BaseMod.addAudio(makeID("BigBirdEyes"), makeSFXPath("Bigbird_Eyes.wav"));
        BaseMod.addAudio(makeID("BigBirdCrunch"), makeSFXPath("Bigbird_HeadCut.wav"));
        BaseMod.addAudio(makeID("BigBirdOpen"), makeSFXPath("Bigbird_MouseOpen.wav"));

        BaseMod.addAudio(makeID("SmallBirdPeck"), makeSFXPath("SmallBird_Atk.wav"));
        BaseMod.addAudio(makeID("SmallBirdPunish"), makeSFXPath("SmallBird_StrongAtk.wav"));
        BaseMod.addAudio(makeID("SmallBirdCage"), makeSFXPath("SmallBird_CageBreak.wav"));

        BaseMod.addAudio(makeID("BlueStarAtk"), makeSFXPath("BlueStar_Atk.wav"));
        BaseMod.addAudio(makeID("BlueStarCharge"), makeSFXPath("BlueStar_Cast.wav"));
        BaseMod.addAudio(makeID("WorshipperSuicide"), makeSFXPath("BlueStar_In.wav"));
        BaseMod.addAudio(makeID("WorshipperAttack"), makeSFXPath("BlueStar_SubAtk.wav"));
        BaseMod.addAudio(makeID("WorshipperExplode"), makeSFXPath("BlueStar_Suicide.wav"));

        BaseMod.addAudio(makeID("SilenceEffect"), makeSFXPath("Clock_NoCreate.wav"));
        BaseMod.addAudio(makeID("SilenceStop"), makeSFXPath("Clock_StopCard.wav"));

        BaseMod.addAudio(makeID("BloodAttack"), makeSFXPath("Bloodbath_Atk.wav"));
        BaseMod.addAudio(makeID("BloodSpecial"), makeSFXPath("Bloodbath_EyeOn.wav"));

        BaseMod.addAudio(makeID("SnowAttack"), makeSFXPath("SnowQueen_Atk.wav"));
        BaseMod.addAudio(makeID("SnowAttackFar"), makeSFXPath("SnowQueen_Atk_Far.wav"));
        BaseMod.addAudio(makeID("SnowBlizzard"), makeSFXPath("SnowQueen_Freeze.wav"));
        BaseMod.addAudio(makeID("SnowPrisonBreak"), makeSFXPath("SnowQueen_IceCrash.wav"));

        BaseMod.addAudio(makeID("BirdSweep"), makeSFXPath("LongBird_SubAtk.wav"));
        BaseMod.addAudio(makeID("BirdShout"), makeSFXPath("LongBird_SubShout.wav"));

        BaseMod.addAudio(makeID("RedMistChange"), makeSFXPath("Kali_Change.ogg"));
        BaseMod.addAudio(makeID("RedMistHori2"), makeSFXPath("Kali_EGO_Hori.ogg"));
        BaseMod.addAudio(makeID("RedMistStab2"), makeSFXPath("Kali_EGO_Stab.ogg"));
        BaseMod.addAudio(makeID("RedMistVert2"), makeSFXPath("Kali_EGO_Vert.ogg"));
        BaseMod.addAudio(makeID("RedMistHori1"), makeSFXPath("Kali_Normal_Hori.ogg"));
        BaseMod.addAudio(makeID("RedMistStab1"), makeSFXPath("Kali_Normal_Stab.ogg"));
        BaseMod.addAudio(makeID("RedMistVert1"), makeSFXPath("Kali_Normal_Vert.ogg"));
        BaseMod.addAudio(makeID("RedMistVertFin"), makeSFXPath("Kali_Special_Vert_Fin.ogg"));
        BaseMod.addAudio(makeID("RedMistVertHit"), makeSFXPath("Kali_Special_Vert_Hit.ogg"));
        BaseMod.addAudio(makeID("RedMistVertCut"), makeSFXPath("Kali_Special_Cut.ogg"));
        BaseMod.addAudio(makeID("RedMistHoriEye"), makeSFXPath("Kali_Special_Hori_Eyeon.ogg"));
        BaseMod.addAudio(makeID("RedMistHoriFin"), makeSFXPath("Kali_Special_Hori_Fin.ogg"));
        BaseMod.addAudio(makeID("RedMistHoriStart"), makeSFXPath("Kali_Special_Hori_Start.ogg"));

        BaseMod.addAudio(makeID("JudgementAttack"), makeSFXPath("LongBird_Down.wav"));
        BaseMod.addAudio(makeID("JudgementHang"), makeSFXPath("LongBird_Hang.wav"));
        BaseMod.addAudio(makeID("JudgementGong"), makeSFXPath("LongBird_On.wav"));
        BaseMod.addAudio(makeID("JudgementDing"), makeSFXPath("LongBird_Stun.wav"));
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

        Atziluth atziluth = new Atziluth();
        atziluth.addAct(TheBeyond.ID);

        CustomIntent.add(new MassAttackIntent());

        //Act 2
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
        BaseMod.addEvent(ChildrenOfTheCity.ID, ChildrenOfTheCity.class, Briah.ID);
        BaseMod.addEvent(NothingThere.ID, NothingThere.class, Briah.ID);
        BaseMod.addEvent(Language.ID, Language.class, Briah.ID);


        // Act 3
        atziluth.addBoss(Twilight.ID, (BaseMod.GetMonster) Twilight::new, makeMonsterPath("Twilight/TwilightMap.png"), makeMonsterPath("Twilight/TwilightMapOutline.png"));
        atziluth.addBoss(Prophet.ID, (BaseMod.GetMonster) Prophet::new, makeMonsterPath("Seraphim/WhiteNightMap.png"), makeMonsterPath("Seraphim/WhiteNightMapOutline.png"));

        BaseMod.addMonster(BigBird.ID, "Big Bird", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new Sage(-600.0F, 0.0F, 0),
                        new Sage(-350.0F, 0.0F, 1),
                        new BigBird(150.0F, 0.0F)
                }));
        BaseMod.addMonster(BlueStar.ID, (BaseMod.GetMonster) BlueStar::new);
        BaseMod.addMonster(SnowQueen.ID, (BaseMod.GetMonster) SnowQueen::new);

        BaseMod.addMonster(PunishingBird.ID, (BaseMod.GetMonster) PunishingBird::new);
        BaseMod.addMonster(BurrowingHeaven.ID, (BaseMod.GetMonster) BurrowingHeaven::new);
        BaseMod.addMonster(PriceOfSilence.ID, "Price of Silence", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new RemnantOfTime(-450.0F, 0.0F),
                        new PriceOfSilence(-50.0F, 0.0F),
                }));
        BaseMod.addMonster(Bloodbath.ID, (BaseMod.GetMonster) Bloodbath::new);
        BaseMod.addMonster(HeartOfAspiration.ID, "Heart of Aspiration", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new LungsOfCraving(-450.0F, 0.0F),
                        new LungsOfCraving(-150.0F, 0.0F),
                        new HeartOfAspiration(150.0F, 0.0F)
                }));
        BaseMod.addMonster(EncounterIDs.BIRDS_3, "3_Birds", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new RunawayBird(-450.0F, 0.0F),
                        new RunawayBird(-200.0F, 0.0F),
                        new EyeballChick(50.0F, 0.0F)
                }));
        BaseMod.addMonster(EncounterIDs.BIRDS_4, "4_Birds", () -> new MonsterGroup(
                new AbstractMonster[] {
                        new RunawayBird(-450.0F, 0.0F),
                        new RunawayBird(-200.0F, 0.0F),
                        new EyeballChick(50.0F, 0.0F),
                        new EyeballChick(300.0F, 0.0F)
                }));
        BaseMod.addMonster(JudgementBird.ID, (BaseMod.GetMonster) JudgementBird::new);
        BaseMod.addMonster(Pinocchio.ID, (BaseMod.GetMonster) Pinocchio::new);
        BaseMod.addMonster(RedMist.ID, (BaseMod.GetMonster) RedMist::new);


        BaseMod.addEvent(RedMistRecollection.ID, RedMistRecollection.class, Atziluth.ID);
        BaseMod.addEvent(HanaAssociation.ID, HanaAssociation.class, Atziluth.ID);
        BaseMod.addEvent(ForThoseWeCherish.ID, ForThoseWeCherish.class, Atziluth.ID);
        BaseMod.addEvent(Philosophy.ID, Philosophy.class, Atziluth.ID);
        BaseMod.addEvent(MindBloom.ID, MindBloom.class, Atziluth.ID);
    }

    private static String makeLocPath(Settings.GameLanguage language, String filename)
    {
        String ret = "localization/";
        switch (language) {
//            case ZHS:
//                ret += "zhs/";
//                break;
//            case JPN:
//                ret += "jpn/";
//                break;
            case RUS:
                ret += "rus/";
                break;
            default:
                ret += "eng/";
                break;
        }
        return getModID() + "Resources/" + (ret + filename + ".json");
    }

    private void loadLocFiles(Settings.GameLanguage language)
    {
        BaseMod.loadCustomStringsFile(CardStrings.class, makeLocPath(language, "Cardstrings"));
        BaseMod.loadCustomStringsFile(EventStrings.class, makeLocPath(language, "Eventstrings"));
        BaseMod.loadCustomStringsFile(MonsterStrings.class, makeLocPath(language, "Monsterstrings"));
        BaseMod.loadCustomStringsFile(RelicStrings.class, makeLocPath(language, "Relicstrings"));
        BaseMod.loadCustomStringsFile(PowerStrings.class, makeLocPath(language, "Powerstrings"));
        BaseMod.loadCustomStringsFile(UIStrings.class, makeLocPath(language, "UIstrings"));
        BaseMod.loadCustomStringsFile(TutorialStrings.class, makeLocPath(language, "Tutorialstrings"));
    }

    @Override
    public void receiveEditStrings()
    {
        loadLocFiles(Settings.GameLanguage.ENG);
        if (Settings.language != Settings.GameLanguage.ENG) {
            loadLocFiles(Settings.language);
        }
    }

    private void loadLocKeywords(Settings.GameLanguage language)
    {
        Gson gson = new Gson();
        String json = Gdx.files.internal(makeLocPath(language, "Keywordstrings")).readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = gson.fromJson(json, Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(getModID().toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        TotalBlockGainedSpireField.totalBlockGained.set(adp(), 0);
    }

    public void receiveEditKeywords() {
        loadLocKeywords(Settings.GameLanguage.ENG);
        if (Settings.language != Settings.GameLanguage.ENG) {
            loadLocKeywords(Settings.language);
        }
    }
}
