package ruina;

import actlikeit.RazIntent.CustomIntent;
import actlikeit.dungeons.CustomDungeon;
import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.ReflectionHacks;
import basemod.eventUtil.AddEventParams;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ruina.CustomIntent.MassAttackIntent;
import ruina.cards.AbstractRuinaCard;
import ruina.cards.cardvars.SecondDamage;
import ruina.cards.cardvars.SecondMagicNumber;
import ruina.dungeons.*;
import ruina.events.NeowAngela;
import ruina.events.act1.*;
import ruina.events.act2.*;
import ruina.events.act3.*;
import ruina.monsters.act1.AllAroundHelper;
import ruina.monsters.act1.Alriune;
import ruina.monsters.act1.Butterflies;
import ruina.monsters.act1.CrazedEmployee;
import ruina.monsters.act1.ForsakenMurderer;
import ruina.monsters.act1.Fragment;
import ruina.monsters.act1.GalaxyFriend;
import ruina.monsters.act1.Orchestra;
import ruina.monsters.act1.Porccubus;
import ruina.monsters.act1.blackSwan.BlackSwan;
import ruina.monsters.act1.blackSwan.Brother;
import ruina.monsters.act1.nothingDer.Gunman;
import ruina.monsters.act1.queenBee.QueenBee;
import ruina.monsters.act1.queenBee.WorkerBee;
import ruina.monsters.act1.redShoes.LeftShoe;
import ruina.monsters.act1.redShoes.RightShoe;
import ruina.monsters.act1.scorchedGirl.MatchFlame;
import ruina.monsters.act1.scorchedGirl.ScorchedGirl;
import ruina.monsters.act1.ShyLook;
import ruina.monsters.act1.TeddyBear;
import ruina.monsters.act1.fairyFestival.FairyQueen;
import ruina.monsters.act1.laetitia.Laetitia;
import ruina.monsters.act1.spiderBud.SpiderBud;
import ruina.monsters.act1.spiderBud.Spiderling;
import ruina.monsters.act2.*;
import ruina.monsters.act2.Jester.JesterOfNihil;
import ruina.monsters.act3.*;
import ruina.monsters.act3.SnowQueen.SnowQueen;
import ruina.monsters.act3.bigBird.BigBird;
import ruina.monsters.act3.bigBird.Sage;
import ruina.monsters.act3.blueStar.BlueStar;
import ruina.monsters.act3.heart.HeartOfAspiration;
import ruina.monsters.act3.heart.LungsOfCraving;
import ruina.monsters.act3.priceOfSilence.PriceOfSilence;
import ruina.monsters.act3.priceOfSilence.RemnantOfTime;
import ruina.monsters.act3.punishingBird.PunishingBird;
import ruina.monsters.act3.seraphim.Prophet;
import ruina.monsters.act3.silentGirl.SilentGirl;
import ruina.monsters.blackSilence.blackSilence1.BlackSilence1;
import ruina.monsters.blackSilence.blackSilence3.Angelica;
import ruina.monsters.blackSilence.blackSilence3.BlackSilence3;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;
import ruina.monsters.eventboss.lulu.monster.Lulu;
import ruina.monsters.eventboss.redMist.monster.RedMist;
import ruina.monsters.eventboss.yan.monster.yanDistortion;
import ruina.monsters.theHead.Baral;
import ruina.monsters.theHead.Zena;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Argalia;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;
import ruina.monsters.uninvitedGuests.normal.bremen.Bremen;
import ruina.monsters.uninvitedGuests.normal.bremen.Netzach;
import ruina.monsters.uninvitedGuests.normal.clown.Oswald;
import ruina.monsters.uninvitedGuests.normal.clown.Tiph;
import ruina.monsters.uninvitedGuests.normal.eileen.Eileen;
import ruina.monsters.uninvitedGuests.normal.eileen.Yesod;
import ruina.monsters.uninvitedGuests.normal.elena.Binah;
import ruina.monsters.uninvitedGuests.normal.elena.Elena;
import ruina.monsters.uninvitedGuests.normal.elena.VermilionCross;
import ruina.monsters.uninvitedGuests.normal.greta.Greta;
import ruina.monsters.uninvitedGuests.normal.greta.Hod;
import ruina.monsters.uninvitedGuests.normal.philip.Malkuth;
import ruina.monsters.uninvitedGuests.normal.philip.Philip;
import ruina.monsters.uninvitedGuests.normal.pluto.monster.Hokma;
import ruina.monsters.uninvitedGuests.normal.pluto.monster.Pluto;
import ruina.monsters.uninvitedGuests.normal.puppeteer.Chesed;
import ruina.monsters.uninvitedGuests.normal.puppeteer.Puppeteer;
import ruina.monsters.uninvitedGuests.normal.tanya.Gebura;
import ruina.monsters.uninvitedGuests.normal.tanya.Tanya;
import ruina.patches.PlayerSpireFields;
import ruina.potions.EgoPotion;
import ruina.relics.AbstractEasyRelic;
import ruina.util.TexLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.util.Properties;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class RuinaMod implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        PostInitializeSubscriber,
        AddAudioSubscriber,
        PostBattleSubscriber,
        PreMonsterTurnSubscriber,
        PostPowerApplySubscriber,
        StartActSubscriber {

    private static final String modID = "ruina";
    public static final TextureAtlas UIAtlas = new TextureAtlas();
    private static Texture silenceImg;

    public static String getModID() {
        return modID;
    }

    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }

    public static class Enums {
        @SpireEnum(name = "EGO") // These two HAVE to have the same absolutely identical name.
        public static AbstractCard.CardColor EGO;
        @SpireEnum(name = "EGO")
        @SuppressWarnings("unused")
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

    public static final Color EGO_POTION_LIQUID = CardHelper.getColor(76, 7, 23);
    public static final Color EGO_POTION_HYBRID = CardHelper.getColor(90, 17, 33);

    //This is for the in-EnemyEnergyPanel mod settings panel.
    private static final String MODNAME = "Ruina";
    private static final String AUTHOR = "Darkglade";
    private static final String DESCRIPTION = "An alternate Act mod inspired by Library of Ruina.";

    // =============== INPUT TEXTURE LOCATION =================

    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "ruinaResources/images/Badge.png";

    public static SpireConfig ruinaConfig;
    private static Logger logger = LogManager.getLogger(RuinaMod.class.getName());

    public static Boolean reverbClear;
    public static Boolean blacksilenceClear;
    public static Boolean headClear;
    public static Boolean clown;


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
            ruinaDefaults.put("reverbClear", false);
            ruinaDefaults.put("blacksilenceClear", false);
            ruinaDefaults.put("headClear", false);
            ruinaDefaults.put("clown", false);
            ruinaConfig = new SpireConfig("Ruina", "RuinaMod", ruinaDefaults);
        } catch (IOException e) {
            logger.error("RuinaMod SpireConfig initialization failed:");
            e.printStackTrace();
        }
        logger.info("RUINA CONFIG OPTIONS LOADED:");
        logger.info("Ally tutorial seen: " + ruinaConfig.getString("Ally Tutorial Seen") + ".");
        LocalDate clownCheck = LocalDate.now();
        clown = clownCheck.getDayOfMonth() == 1 && clownCheck.getMonth() == Month.APRIL;
        loadConfig();
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
    public static String makeVfxPath(String resourcePath) {
        return modID + "Resources/images/vfx/" + resourcePath;
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

    public static String makeScenePath(String resourcePath) {
        return modID + "Resources/images/scene/" + resourcePath;
    }

    public static String makeEventPath(String resourcePath) {
        return modID + "Resources/images/events/" + resourcePath;
    }

    public static void initialize() { RuinaMod ruinaMod = new RuinaMod(); }

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
        BaseMod.addAudio(makeID("NosSpecialEye"), makeSFXPath("Nosferatu_Changed_StrongAtk_Eye.wav"));

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
        BaseMod.addAudio(makeID("BluntHori"), makeSFXPath("Blow_Hori.wav"));
        BaseMod.addAudio(makeID("BluntVert"), makeSFXPath("Blow_Vert.wav"));

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

        BaseMod.addAudio(makeID("YanLock"), makeSFXPath("Yan_Typing_Atk.wav"));
        BaseMod.addAudio(makeID("DistortedBladeFinish"), makeSFXPath("Yan_GreatSword_Finish.wav"));
        BaseMod.addAudio(makeID("DistortedBladeStart"), makeSFXPath("Yan_GreatSword_Start.wav"));
        BaseMod.addAudio(makeID("YanVert"), makeSFXPath("Yan_Lib_Vert.wav"));
        BaseMod.addAudio(makeID("YanStab"), makeSFXPath("Yan_Stab.wav"));
        BaseMod.addAudio(makeID("YanBrand"), makeSFXPath("Yan_Stigma_Atk.wav"));

        BaseMod.addAudio(makeID("SwordStab"), makeSFXPath("Sword_Stab.wav"));
        BaseMod.addAudio(makeID("SwordVert"), makeSFXPath("Sword_Vert.wav"));
        BaseMod.addAudio(makeID("SwordHori"), makeSFXPath("Sword_Hori.wav"));

        BaseMod.addAudio(makeID("DisposalUp"), makeSFXPath("Nicolrai_Special_SwordUp.wav"));
        BaseMod.addAudio(makeID("DisposalDown"), makeSFXPath("Nicolrai_Special_SwordDown.wav"));
        BaseMod.addAudio(makeID("DisposalFinish"), makeSFXPath("Nicolrai_Special_Cut.wav"));
        BaseMod.addAudio(makeID("DisposalBlood"), makeSFXPath("Nicolrai_Special_Blood.wav"));

        BaseMod.addAudio(makeID("PuppetStart"), makeSFXPath("Puppet_StrongStart.wav"));
        BaseMod.addAudio(makeID("PuppetStrongAtk"), makeSFXPath("Puppet_StrongAtk.wav"));
        BaseMod.addAudio(makeID("PuppetBreak"), makeSFXPath("Puppet_Break.wav"));

        BaseMod.addAudio(makeID("RolandAxe"), makeSFXPath("Roland_Axe.wav"));
        BaseMod.addAudio(makeID("RolandDualSword"), makeSFXPath("Roland_DuelSword.wav"));
        BaseMod.addAudio(makeID("RolandDualSwordStrong"), makeSFXPath("Roland_DuelSword_Strong.wav"));
        BaseMod.addAudio(makeID("RolandDuralandalDown"), makeSFXPath("Roland_Duralandal_Down.wav"));
        BaseMod.addAudio(makeID("RolandDuralandalStrong"), makeSFXPath("Roland_Duralandal_Strong.wav"));
        BaseMod.addAudio(makeID("RolandDuralandalUp"), makeSFXPath("Roland_Duralandal_Up.wav"));
        BaseMod.addAudio(makeID("RolandGreatSword"), makeSFXPath("Roland_GreatSword.wav"));
        BaseMod.addAudio(makeID("RolandLongSwordAtk"), makeSFXPath("Roland_LongSword_Atk.wav"));
        BaseMod.addAudio(makeID("RolandLongSwordFin"), makeSFXPath("Roland_LongSword_Fin.wav"));
        BaseMod.addAudio(makeID("RolandLongSwordStart"), makeSFXPath("Roland_LongSword_Start.wav"));
        BaseMod.addAudio(makeID("RolandMace"), makeSFXPath("Roland_Mace.wav"));
        BaseMod.addAudio(makeID("RolandRevolver"), makeSFXPath("Roland_Revolver.wav"));
        BaseMod.addAudio(makeID("RolandShortSword"), makeSFXPath("Roland_ShortSword.wav"));
        BaseMod.addAudio(makeID("RolandShotgun"), makeSFXPath("Roland_Shotgun.wav"));

        BaseMod.addAudio(makeID("ArgaliaStrongAtk2"), makeSFXPath("Blue_Argalria_Strong_Atk2.wav"));
        BaseMod.addAudio(makeID("ArgaliaAtk"), makeSFXPath("Blue_Argalria_Atk.wav"));
        BaseMod.addAudio(makeID("ArgaliaFarAtk1"), makeSFXPath("Blue_Argalria_Far_Atk1.wav"));
        BaseMod.addAudio(makeID("ArgaliaFarAtk2"), makeSFXPath("Blue_Argalria_Far_Atk2.wav"));
        BaseMod.addAudio(makeID("ArgaliaStrongAtk1"), makeSFXPath("Blue_Argalria_Strong_Atk1.wav"));

        BaseMod.addAudio(makeID("BinahStoneReady"), makeSFXPath("Binah_Stone_Ready.wav"));
        BaseMod.addAudio(makeID("BinahStoneFire"), makeSFXPath("Binah_Stone_Fire.wav"));
        BaseMod.addAudio(makeID("BinahChain"), makeSFXPath("Binah_Chain.wav"));
        BaseMod.addAudio(makeID("BinahFairy"), makeSFXPath("Binah_Fairy.wav"));

        BaseMod.addAudio(makeID("ElenaStrongUp"), makeSFXPath("Elena_StrongUp.wav"));
        BaseMod.addAudio(makeID("ElenaStrongAtk"), makeSFXPath("Elena_StrongAtk.wav"));
        BaseMod.addAudio(makeID("ElenaStrongStart"), makeSFXPath("Elena_StrongStart.wav"));

        BaseMod.addAudio(makeID("FireVert"), makeSFXPath("Riu_Vert.wav"));
        BaseMod.addAudio(makeID("FireHori"), makeSFXPath("Riu_Hori.wav"));
        BaseMod.addAudio(makeID("FireStab"), makeSFXPath("Riu_Stab.wav"));
        BaseMod.addAudio(makeID("FireGuard"), makeSFXPath("Riu_Guard.wav"));
        BaseMod.addAudio(makeID("FireStrong"), makeSFXPath("Riu_Strong.wav"));

        BaseMod.addAudio(makeID("PlutoVert"), makeSFXPath("Pluto_Vert.wav"));
        BaseMod.addAudio(makeID("PlutoHori"), makeSFXPath("Pluto_Hori.wav"));
        BaseMod.addAudio(makeID("PlutoStab"), makeSFXPath("Pluto_Stab.wav"));
        BaseMod.addAudio(makeID("PlutoGuard"), makeSFXPath("Pluto_Guard.wav"));
        BaseMod.addAudio(makeID("PlutoContract"), makeSFXPath("Pluto_Contract.wav"));
        BaseMod.addAudio(makeID("PlutoStrong"), makeSFXPath("Pluto_StrongAtk.wav"));
        BaseMod.addAudio(makeID("PlutoStrongStart"), makeSFXPath("Pluto_StrongStart.wav"));

        BaseMod.addAudio(makeID("OswaldVert"), makeSFXPath("Oswald_Vert.wav"));
        BaseMod.addAudio(makeID("OswaldAttract"), makeSFXPath("Oswald_Attract.wav"));
        BaseMod.addAudio(makeID("OswaldHori"), makeSFXPath("Oswald_Hori.wav"));
        BaseMod.addAudio(makeID("OswaldStab"), makeSFXPath("Oswald_Stab.wav"));
        BaseMod.addAudio(makeID("OswaldFinish"), makeSFXPath("Oswald_Strong_Up.wav"));
        BaseMod.addAudio(makeID("OswaldLaugh"), makeSFXPath("Oswald_Standby.wav"));

        BaseMod.addAudio(makeID("HanaBlunt"), makeSFXPath("Hana_Blow.wav"));
        BaseMod.addAudio(makeID("HanaStab"), makeSFXPath("Hana_Stab.wav"));

        BaseMod.addAudio(makeID("BremenStrongFar"), makeSFXPath("Bremen_StrongFar.wav"));
        BaseMod.addAudio(makeID("BremenStrong"), makeSFXPath("Bremen_Strong.wav"));
        BaseMod.addAudio(makeID("BremenChicken"), makeSFXPath("Bremen_Chicken.wav"));
        BaseMod.addAudio(makeID("BremenDog"), makeSFXPath("Bremen_Dog.wav"));
        BaseMod.addAudio(makeID("BremenHorse"), makeSFXPath("Bremen_Horse.wav"));

        BaseMod.addAudio(makeID("PhilipTransform"), makeSFXPath("Philip_FilterOn.wav"));
        BaseMod.addAudio(makeID("PhilipVert"), makeSFXPath("Philip_Vert.wav"));
        BaseMod.addAudio(makeID("PhilipHori"), makeSFXPath("Philip_Hori.wav"));
        BaseMod.addAudio(makeID("PhilipStab"), makeSFXPath("Philip_Stab.wav"));
        BaseMod.addAudio(makeID("PhilipStrong"), makeSFXPath("Philip_Strong.wav"));
        BaseMod.addAudio(makeID("PhilipExplosion"), makeSFXPath("Cry_FarExplosion.wav"));
        BaseMod.addAudio(makeID("CryHori"), makeSFXPath("Cry_Kid_Hori.wav"));
        BaseMod.addAudio(makeID("CryStab"), makeSFXPath("Cry_Kid_Stab.wav"));

        BaseMod.addAudio(makeID("XiaoVert"), makeSFXPath("Xiao_Vert.wav"));
        BaseMod.addAudio(makeID("XiaoHori"), makeSFXPath("Xiao_Hori.wav"));
        BaseMod.addAudio(makeID("XiaoStab"), makeSFXPath("Xiao_Stab.wav"));
        BaseMod.addAudio(makeID("XiaoStart"), makeSFXPath("Xiao_LandHit_Charge.wav"));
        BaseMod.addAudio(makeID("XiaoFin"), makeSFXPath("Xiao_LandHit_Hit.wav"));
        BaseMod.addAudio(makeID("XiaoRoar"), makeSFXPath("Xiao_Roar.wav"));
        BaseMod.addAudio(makeID("XiaoStrongFin"), makeSFXPath("Xiao_Strong_Hori.wav"));
        BaseMod.addAudio(makeID("XiaoStrongStart"), makeSFXPath("Xiao_Strong_Upper.wav"));

        BaseMod.addAudio(makeID("GretaEat"), makeSFXPath("Greta_Eat.wav"));

        BaseMod.addAudio(makeID("PurpleStab2"), makeSFXPath("Purple_Stab_Stab2.wav"));
        BaseMod.addAudio(makeID("PurpleStab1"), makeSFXPath("Purple_Stab_Stab1.wav"));
        BaseMod.addAudio(makeID("PurpleGuard"), makeSFXPath("Purple_Guard.wav"));
        BaseMod.addAudio(makeID("PurpleChange"), makeSFXPath("Purple_Change.wav"));
        BaseMod.addAudio(makeID("PurpleBlunt"), makeSFXPath("Purple_Hit_Vert.wav"));
        BaseMod.addAudio(makeID("PurpleSlashHori"), makeSFXPath("Purple_Slash_Hori.wav"));
        BaseMod.addAudio(makeID("PurpleSlashVert"), makeSFXPath("Purple_Slash_VertDown.wav"));

        BaseMod.addAudio(makeID("BulletFlame"), makeSFXPath("Matan_Flame.wav"));
        BaseMod.addAudio(makeID("BulletFinalShot"), makeSFXPath("Matan_FinalShot.wav"));

        BaseMod.addAudio(makeID("GearStrongStart"), makeSFXPath("Eilin_StrongStart.wav"));
        BaseMod.addAudio(makeID("GearStrongAtk"), makeSFXPath("Eilin_StrongAtk.wav"));
        BaseMod.addAudio(makeID("GearFar"), makeSFXPath("Eilin_FarAtk.wav"));
        BaseMod.addAudio(makeID("GearVert"), makeSFXPath("Blue_Gear_Vert.wav"));

        BaseMod.addAudio(makeID("ClawUp"), makeSFXPath("Claw_UpAtk.wav"));
        BaseMod.addAudio(makeID("ClawCutscene"), makeSFXPath("Claw_Blue_CutScene.wav"));
        BaseMod.addAudio(makeID("ClawFin"), makeSFXPath("Claw_Blue_Fin.wav"));
        BaseMod.addAudio(makeID("ClawDown"), makeSFXPath("Claw_DownAtk.wav"));
        BaseMod.addAudio(makeID("ClawInjection"), makeSFXPath("Claw_Injection.wav"));
        BaseMod.addAudio(makeID("ClawStab"), makeSFXPath("Claw_Stab.wav"));
        BaseMod.addAudio(makeID("ClawUltiEnd"), makeSFXPath("Claw_Ulti_End.wav"));
        BaseMod.addAudio(makeID("ClawUltiMove"), makeSFXPath("Claw_Ulti_Move.wav"));

        BaseMod.addAudio(makeID("ZenaBoldLine"), makeSFXPath("Abiter_BoldLine.wav"));
        BaseMod.addAudio(makeID("ZenaNormalLine"), makeSFXPath("Abiter_NormalLine.wav"));
        BaseMod.addAudio(makeID("ZenaThinLine"), makeSFXPath("Abiter_ThinLine.wav"));
        BaseMod.addAudio(makeID("ZenaBoom"), makeSFXPath("Abiter_Special_Boom.wav"));
        BaseMod.addAudio(makeID("ZenaCutscene"), makeSFXPath("Abiter_Special_CutScene.wav"));
        BaseMod.addAudio(makeID("ZenaStart"), makeSFXPath("Abiter_Special_Start.wav"));
        BaseMod.addAudio(makeID("Shaking"), makeSFXPath("Shake_End.wav"));
        BaseMod.addAudio(makeID("BinahArrive"), makeSFXPath("Binah_On.wav"));
        BaseMod.addAudio(makeID("GeburaArrive"), makeSFXPath("Gebura_On.wav"));

        BaseMod.addAudio(makeID("SilentEye"), makeSFXPath("Silentgirl_Eye.wav"));
        BaseMod.addAudio(makeID("SilentHammer"), makeSFXPath("Silentgirl_Hammer.wav"));
        BaseMod.addAudio(makeID("SilentPhaseChange"), makeSFXPath("Silentgirl_PhaseChange.wav"));
        BaseMod.addAudio(makeID("SilentNail"), makeSFXPath("Silentgirl_Volt.wav"));

        BaseMod.addAudio(makeID("HelperOn"), makeSFXPath("Helper_On.wav"));
        BaseMod.addAudio(makeID("HelperCharge"), makeSFXPath("Helper_FullCharge.wav"));

        BaseMod.addAudio(makeID("MatchExplode"), makeSFXPath("MatchGirl_Explosion.wav"));
        BaseMod.addAudio(makeID("MatchSizzle"), makeSFXPath("MatchGirl_Barrier.wav"));

        BaseMod.addAudio(makeID("TeddyOn"), makeSFXPath("Teddy_On.wav"));
        BaseMod.addAudio(makeID("TeddyBlock"), makeSFXPath("Teddy_Guard.wav"));
        BaseMod.addAudio(makeID("TeddyAtk"), makeSFXPath("Teddy_NormalAtk.wav"));

        BaseMod.addAudio(makeID("ShyAtk"), makeSFXPath("Shy_Atk.wav"));

        BaseMod.addAudio(makeID("AlriuneHori"), makeSFXPath("Ali_Boss_Hori.wav"));
        BaseMod.addAudio(makeID("AlriuneGuard"), makeSFXPath("Ali_Guard.wav"));

        BaseMod.addAudio(makeID("OrchestraFinale"), makeSFXPath("Sym_movement_5_finale.wav"));
        BaseMod.addAudio(makeID("OrchestraMovement1"), makeSFXPath("Sym_Chor_Atk.wav"));
        BaseMod.addAudio(makeID("OrchestraMovement2"), makeSFXPath("Sym_movement_5.wav"));
        BaseMod.addAudio(makeID("OrchestraClap"), makeSFXPath("Sym_movement_0_clap.wav"));

        BaseMod.addAudio(makeID("FairySpecial"), makeSFXPath("Fairy_Special.wav"));
        BaseMod.addAudio(makeID("FairyMinionAtk"), makeSFXPath("Fairy_MiniAtk.wav"));
        BaseMod.addAudio(makeID("FairyQueenAtk"), makeSFXPath("Fairy_QueenAtk.wav"));
        BaseMod.addAudio(makeID("FairyQueenChange"), makeSFXPath("Fairy_QueenChange.wav"));
        BaseMod.addAudio(makeID("FairyQueenEat"), makeSFXPath("Fairy_QueenEat.wav"));

        BaseMod.addAudio(makeID("FragmentStab"), makeSFXPath("Cosmos_Stab_Down.wav"));
        BaseMod.addAudio(makeID("FragmentSing"), makeSFXPath("Cosmos_Sing.wav"));

        BaseMod.addAudio(makeID("ButterflyAtk"), makeSFXPath("ButterFlyMan_ButterflyAtk.wav"));

        BaseMod.addAudio(makeID("LaetitiaAtk"), makeSFXPath("Laetitia_Atk.wav"));
        BaseMod.addAudio(makeID("LaetitiaFriendAtk"), makeSFXPath("Laetitia_Friend_Stab.wav"));
        BaseMod.addAudio(makeID("Goodbye"), makeSFXPath("NothingThere_Goodbye.wav"));

        BaseMod.addAudio(makeID("HeavenWakeStrong"), makeSFXPath("MustSee_Wake_Strong.wav"));
        BaseMod.addAudio(makeID("HeavenNosee1"), makeSFXPath("MustSee_Nosee1.wav"));

        BaseMod.addAudio(makeID("PorccuStrongStab2"), makeSFXPath("Porccu_Strong_Stab2.wav"));
        BaseMod.addAudio(makeID("PorccuPenetrate"), makeSFXPath("Porccu_Penetrate.wav"));

        BaseMod.addAudio(makeID("ShoesOn"), makeSFXPath("RedShoes_On3.wav"));
        BaseMod.addAudio(makeID("ShoesAtk"), makeSFXPath("RedShoes_Atk.wav"));

        BaseMod.addAudio(makeID("GalaxyDef"), makeSFXPath("GalaxyBoy_FriendDef.wav"));
        BaseMod.addAudio(makeID("GalaxyAtk"), makeSFXPath("GalaxyBoy_FriendAtk.wav"));

        BaseMod.addAudio(makeID("SpiderStrongAtk"), makeSFXPath("Spidermom_Strong_Hori.wav"));
        BaseMod.addAudio(makeID("SpiderBabyAtk"), makeSFXPath("Spidermom_Babyatk.wav"));
        BaseMod.addAudio(makeID("SpiderDown"), makeSFXPath("Spidermom_Down.wav"));
        BaseMod.addAudio(makeID("SpiderProtect"), makeSFXPath("Spidermom_Protect.wav"));

        BaseMod.addAudio(makeID("QueenBeeStab"), makeSFXPath("QueenBee_Queen_Stab.wav"));
        BaseMod.addAudio(makeID("QueenBeeBuff"), makeSFXPath("QueenBee_AtkBuff.wav"));
        BaseMod.addAudio(makeID("QueenBeeLegAtk"), makeSFXPath("QueenBee_BeeAtk_leg.wav"));

        BaseMod.addAudio(makeID("SwanVertDown"), makeSFXPath("BlackSwan_VertDown.wav"));
        BaseMod.addAudio(makeID("SwanGuard"), makeSFXPath("BlackSwan_Guard.wav"));
        BaseMod.addAudio(makeID("SwanPierce"), makeSFXPath("BlackSwan_Pierce.wav"));
        BaseMod.addAudio(makeID("SwanRevive"), makeSFXPath("BlackSwan_Revive.wav"));
        BaseMod.addAudio(makeID("SwanShout"), makeSFXPath("BlackSwan_Shout.wav"));

        BaseMod.addAudio(makeID("FuneralSpecial"), makeSFXPath("ButterFlyMan_Special1.wav"));
        BaseMod.addAudio(makeID("FuneralAtkBlack"), makeSFXPath("ButterFlyMan_Atk_Black.wav"));
        BaseMod.addAudio(makeID("FuneralAtkWhite"), makeSFXPath("ButterFlyMan_Atk_White.wav"));

        BaseMod.addAudio(makeID("SingingEat"), makeSFXPath("SingingMachine_Eat.wav"));
        BaseMod.addAudio(makeID("SingingRhythm"), makeSFXPath("Singing_Rhythm.wav"));

        BaseMod.addAudio(makeID("FingerSnap"), makeSFXPath("Finger_Snapping.wav"));

        BaseMod.addAudio(makeID("NothingStrong"), makeSFXPath("NothingThere_Strong_Flesh.wav"));
        BaseMod.addAudio(makeID("NothingChange"), makeSFXPath("NothingThere_Change.wav"));
        BaseMod.addAudio(makeID("NothingHello"), makeSFXPath("NothingThere_Hello.wav"));
        BaseMod.addAudio(makeID("NothingNormal"), makeSFXPath("NothingThere_Normal_Flesh.wav"));
        BaseMod.addAudio(makeID("BulletShot"), makeSFXPath("Matan_NormalShot.wav"));
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
        receiveEditPotions();
        //Add WidePotion Compatibility
        if (Loader.isModLoaded("widepotions")) {
            logger.info("Wide Potions: Detected. Loading WIDE POTIONS.");
            WidePotionsMod.whitelistSimplePotion(EgoPotion.POTION_ID);
        }

        silenceImg = new Texture(makeUIPath("silenceImg.png"));
        UIAtlas.addRegion("silenceImg", silenceImg, 0, 0, silenceImg.getWidth(), silenceImg.getHeight());

        // Load the Mod Badge
        Texture badgeTexture = TexLoader.getTexture(BADGE_IMAGE);

        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();

        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        Asiyah asiyah = new Asiyah();
        asiyah.addAct(Exordium.ID);

        Briah briah = new Briah();
        briah.addAct(TheCity.ID);

        Atziluth atziluth = new Atziluth();
        atziluth.addAct(TheBeyond.ID);

        UninvitedGuests uninvitedGuests = new UninvitedGuests();
        uninvitedGuests.addAct(TheEnding.ID);

        UninvitedGuestsShort uninvitedGuestsShort = new UninvitedGuestsShort();
        uninvitedGuestsShort.addAct(TheEnding.ID);

        BlackSilence silence = new BlackSilence();
        CustomDungeon.addAct(5, silence);

        CustomIntent.add(new MassAttackIntent());

        //Act 1
        BaseMod.addMonster(AllAroundHelper.ID, "All_Around_Helper", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new AllAroundHelper(-300.0F, 0.0F, true),
                        new AllAroundHelper(0.0F, 0.0F, false),
                }));
        BaseMod.addMonster(Alriune.ID, (BaseMod.GetMonster) Alriune::new);
        BaseMod.addMonster(Laetitia.ID, (BaseMod.GetMonster) Laetitia::new);

        BaseMod.addMonster(ForsakenMurderer.ID, (BaseMod.GetMonster) ForsakenMurderer::new);
        BaseMod.addMonster(ScorchedGirl.ID, "ScorchedGirl", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new MatchFlame(-450.0F, 0.0F),
                        new MatchFlame(-200.0F, 0.0F),
                        new ScorchedGirl(50.0F, 0.0F)
                }));
        BaseMod.addMonster(TeddyBear.ID, (BaseMod.GetMonster) TeddyBear::new);
        BaseMod.addMonster(ShyLook.ID, (BaseMod.GetMonster) ShyLook::new);
        BaseMod.addMonster(Fragment.ID, (BaseMod.GetMonster) Fragment::new);
        BaseMod.addMonster(EncounterIDs.EMPLOYEES_2, "2_Employees", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new CrazedEmployee(-200.0F, 0.0F, 0),
                        new CrazedEmployee(50.0F, 0.0F, 1),
                }));
        BaseMod.addMonster(EncounterIDs.EMPLOYEES_3, "3_Employees", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new CrazedEmployee(-450.0F, 0.0F, 0),
                        new CrazedEmployee(-200.0F, 0.0F, 1),
                        new CrazedEmployee(50.0F, 0.0F, 2)
                }));
        float butterflyY = 100.0f;
        BaseMod.addMonster(EncounterIDs.BUTTERFLIES_3, "3_Butterflies", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Butterflies(-450.0F, butterflyY),
                        new Butterflies(-200.0F, butterflyY),
                        new Butterflies(50.0F, butterflyY)
                }));
        BaseMod.addMonster(EncounterIDs.BUTTERFLIES_5, "5_Butterflies", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Butterflies(-450.0F, butterflyY),
                        new Butterflies(-300.0F, butterflyY),
                        new Butterflies(-150.0F, butterflyY),
                        new Butterflies(0.0f, butterflyY),
                        new Butterflies(150.0F, butterflyY)
                }));
        BaseMod.addMonster(Porccubus.ID, (BaseMod.GetMonster) Porccubus::new);
        BaseMod.addMonster(EncounterIDs.RED_SHOES, "Red_Shoes", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new LeftShoe(-350.0F, 0.0F),
                        new RightShoe(0.0F, 0.0F),
                }));
        BaseMod.addMonster(GalaxyFriend.ID, "Galaxy_Friend", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new GalaxyFriend(-350.0F, 0.0F),
                        new GalaxyFriend(0.0F, 0.0F),
                }));
        BaseMod.addMonster(SpiderBud.ID, "Spider_Bud", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Spiderling(-450.0F, 0.0f),
                        new Spiderling(-200.0F, 0.0f),
                        new Spiderling(50.0f, 0.0f),
                        new SpiderBud(300.0F, 0.0f)
                }));
        BaseMod.addMonster(QueenBee.ID, "Queen_Bee", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new WorkerBee(-350.0F, 0.0F),
                        new QueenBee(0.0F, 0.0F),
                }));

        BaseMod.addMonster(Lulu.ID, (BaseMod.GetMonster) Lulu::new);

        asiyah.addBoss(FairyQueen.ID, (BaseMod.GetMonster) FairyQueen::new, makeMonsterPath("FairyQueen/FairyMapIcon.png"), makeMonsterPath("FairyQueen/FairyMapIconOutline.png"));
        asiyah.addBoss(EncounterIDs.NOTHING_DER, () -> new MonsterGroup(
                new AbstractMonster[]{
                        new ruina.monsters.act1.nothingDer.NothingThere(-1000.0F, 0.0F),
                        new Gunman(70.0F, 0.0F),
                }), makeMonsterPath("Gunman/GunMap.png"), makeMonsterPath("Gunman/GunIcon.png"));
        asiyah.addBoss(BlackSwan.ID, () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Brother(-450.0F, 0.0F, 1),
                        new Brother(-250.0F, 0.0F, 2),
                        new BlackSwan(-50.0F, 0.0F),
                        new Brother(150.0F, 0.0F, 3),
                        new Brother(350.0F, 0.0F, 4),
                }), makeMonsterPath("BlackSwan/SwanMap.png"), makeMonsterPath("BlackSwan/SwanMapOutline.png"));
        asiyah.addBoss(Orchestra.ID, (BaseMod.GetMonster) Orchestra::new, makeMonsterPath("Orchestra/OrchestraMap.png"), makeMonsterPath("Orchestra/OrchestraMap.png"));


        BaseMod.addEvent(ShiAssociation.ID, ShiAssociation.class, Asiyah.ID);
        BaseMod.addEvent(WarpTrain.ID, WarpTrain.class, Asiyah.ID);
        BaseMod.addEvent(new AddEventParams.Builder(StreetlightOffice.ID, StreetlightOffice.class)
                //Prevent from appearing too early//
                .bonusCondition(() -> (AbstractDungeon.floorNum > 6))
                .dungeonID(Asiyah.ID)
                .create());
        BaseMod.addEvent(NightInTheBackstreets.ID, NightInTheBackstreets.class, Asiyah.ID);
        BaseMod.addEvent(SnowWhiteApple.ID, SnowWhiteApple.class, Asiyah.ID);
        BaseMod.addEvent(DerFreischutz.ID, DerFreischutz.class, Asiyah.ID);
        BaseMod.addEvent(UnknownPath.ID, UnknownPath.class, Asiyah.ID);
        BaseMod.addEvent(new AddEventParams.Builder(SingingMachine.ID, SingingMachine.class)
                .bonusCondition(SingingMachine::hasValidCards)
                .dungeonID(Asiyah.ID)
                .create());
        BaseMod.addEvent(GalaxyChild.ID, GalaxyChild.class, Asiyah.ID);
        BaseMod.addEvent(Funeral.ID, Funeral.class, Asiyah.ID);
        BaseMod.addEvent(PatronLibrarian.ID, PatronLibrarian.class, Asiyah.ID);


        //Act 2
        BaseMod.addMonster(Mountain.ID, (BaseMod.GetMonster) Mountain::new);
        BaseMod.addMonster(RoadHome.ID, "Road_Home", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new HomeAlly(-500.0F, 0.0F),
                        new ScaredyCat(-100.0F, 0.0F),
                        new RoadHome(200.0F, 0.0F),
                }));
        BaseMod.addMonster(ServantOfWrath.ID, "Servant_of_Wrath", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new ServantOfWrath(-500.0F, 0.0F),
                        new Hermit(100.0F, 0.0F),
                }));

        BaseMod.addMonster(EncounterIDs.SCARECROWS_2, "2_Scarecrows", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Scarecrow(-450.0F, 0.0F, 1),
                        new Scarecrow(-150.0F, 0.0F, 0),
                }));
        BaseMod.addMonster(EncounterIDs.SCARECROWS_3, "3_Scarecrowss", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Scarecrow(-450.0F, 0.0F, 2),
                        new Scarecrow(-200.0F, 0.0F, 1),
                        new Scarecrow(50.0F, 0.0F, 0)
                }));
        BaseMod.addMonster(Woodsman.ID, (BaseMod.GetMonster) Woodsman::new);
        BaseMod.addMonster(EncounterIDs.BATS_3, "3_Bats", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new SanguineBat(-450.0F, 0.0F),
                        new SanguineBat(-200.0F, 0.0F),
                        new SanguineBat(50.0F, 0.0F)
                }));
        BaseMod.addMonster(Nosferatu.ID, (BaseMod.GetMonster) Nosferatu::new);
        BaseMod.addMonster(EncounterIDs.NOS_AND_BAT, "Nos_and_Bat", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new SanguineBat(-450.0F, 0.0F),
                        new Nosferatu(-150.0F, 0.0F),
                }));
        BaseMod.addMonster(KnightOfDespair.ID, (BaseMod.GetMonster) KnightOfDespair::new);
        BaseMod.addMonster(KingOfGreed.ID, (BaseMod.GetMonster) KingOfGreed::new);
        BaseMod.addMonster(BadWolf.ID, (BaseMod.GetMonster) BadWolf::new);
        BaseMod.addMonster(QueenOfHate.ID, (BaseMod.GetMonster) QueenOfHate::new);

        briah.addBoss(EncounterIDs.RED_AND_WOLF, () -> new MonsterGroup(
                new AbstractMonster[]{
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
        BaseMod.addEvent(PatronLibrarian.ID, PatronLibrarian.class, Briah.ID);


        // Act 3
        atziluth.addBoss(Twilight.ID, (BaseMod.GetMonster) Twilight::new, makeMonsterPath("Twilight/TwilightMap.png"), makeMonsterPath("Twilight/TwilightMapOutline.png"));
        atziluth.addBoss(Prophet.ID, (BaseMod.GetMonster) Prophet::new, makeMonsterPath("Seraphim/WhiteNightMap.png"), makeMonsterPath("Seraphim/WhiteNightMapOutline.png"));
        atziluth.addBoss(SilentGirl.ID, (BaseMod.GetMonster) SilentGirl::new, makeMonsterPath("SilentGirl/MapIcon.png"), makeMonsterPath("SilentGirl/MapIcon.png"));

        BaseMod.addMonster(BigBird.ID, "Big Bird", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Sage(-600.0F, 0.0F, 0),
                        new Sage(-350.0F, 0.0F, 1),
                        new BigBird(150.0F, 0.0F)
                }));
        BaseMod.addMonster(BlueStar.ID, (BaseMod.GetMonster) BlueStar::new);
        BaseMod.addMonster(SnowQueen.ID, (BaseMod.GetMonster) SnowQueen::new);

        BaseMod.addMonster(PunishingBird.ID, (BaseMod.GetMonster) PunishingBird::new);
        BaseMod.addMonster(BurrowingHeaven.ID, (BaseMod.GetMonster) BurrowingHeaven::new);
        BaseMod.addMonster(PriceOfSilence.ID, "Price of Silence", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new RemnantOfTime(-450.0F, 0.0F),
                        new PriceOfSilence(-50.0F, 0.0F),
                }));
        BaseMod.addMonster(Bloodbath.ID, (BaseMod.GetMonster) Bloodbath::new);
        BaseMod.addMonster(HeartOfAspiration.ID, "Heart of Aspiration", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new LungsOfCraving(-450.0F, 0.0F),
                        new LungsOfCraving(-150.0F, 0.0F),
                        new HeartOfAspiration(150.0F, 0.0F)
                }));
        BaseMod.addMonster(EncounterIDs.BIRDS_3, "3_Birds", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new RunawayBird(-450.0F, 0.0F),
                        new RunawayBird(-200.0F, 0.0F),
                        new EyeballChick(50.0F, 0.0F)
                }));
        BaseMod.addMonster(EncounterIDs.BIRDS_4, "4_Birds", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new RunawayBird(-450.0F, 0.0F),
                        new RunawayBird(-200.0F, 0.0F),
                        new EyeballChick(50.0F, 0.0F),
                        new EyeballChick(300.0F, 0.0F)
                }));
        BaseMod.addMonster(JudgementBird.ID, (BaseMod.GetMonster) JudgementBird::new);
        BaseMod.addMonster(Pinocchio.ID, (BaseMod.GetMonster) Pinocchio::new);

        BaseMod.addMonster(RedMist.ID, (BaseMod.GetMonster) RedMist::new);
        BaseMod.addMonster(yanDistortion.ID, (BaseMod.GetMonster) yanDistortion::new);


        BaseMod.addEvent(RedMistRecollection.ID, RedMistRecollection.class, Atziluth.ID);
        BaseMod.addEvent(HanaAssociation.ID, HanaAssociation.class, Atziluth.ID);
        BaseMod.addEvent(ForThoseWeCherish.ID, ForThoseWeCherish.class, Atziluth.ID);
        BaseMod.addEvent(Philosophy.ID, Philosophy.class, Atziluth.ID);
        BaseMod.addEvent(TheThumb.ID, TheThumb.class, Atziluth.ID);
        BaseMod.addEvent(CryingChildren.ID, CryingChildren.class, Atziluth.ID);
        BaseMod.addEvent(DistortedYan.ID, DistortedYan.class, Atziluth.ID);
        BaseMod.addEvent(PatronLibrarian.ID, PatronLibrarian.class, Atziluth.ID);
        BaseMod.addEvent(YesterdayPromise.ID, YesterdayPromise.class, Atziluth.ID);

        //Uninvited Guests
        BaseMod.addMonster(Puppeteer.ID, "Puppeteer", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Chesed(-550.0F, 0.0F),
                        new Puppeteer(200.0F, 0.0F),
                }));
        BaseMod.addMonster(Tanya.ID, "Tanya", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Gebura(-500.0F, 0.0F),
                        new Tanya(0.0F, 0.0F),
                }));
        BaseMod.addMonster(Elena.ID, "Elena", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Binah(-550.0F, 0.0F),
                        new VermilionCross(-100.0F, 0.0F),
                        new Elena(200.0F, 0.0F),
                }));
        BaseMod.addMonster(Oswald.ID, "Oswald", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Tiph(-500.0F, 0.0F),
                        new Oswald(0.0F, 0.0F),
                }));
        BaseMod.addMonster(Bremen.ID, "Bremen", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Netzach(-500.0F, 0.0F),
                        new Bremen(0.0F, 0.0F),
                }));
        BaseMod.addMonster(Philip.ID, "Philip", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Malkuth(-550.0F, 0.0F),
                        new Philip(200.0F, 0.0F),
                }));
        BaseMod.addMonster(Greta.ID, "Greta", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Hod(-550.0F, 0.0F),
                        new Greta(150.0F, 0.0F),
                }));
        BaseMod.addMonster(Eileen.ID, "Eileen", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Yesod(-550.0F, 0.0F),
                        new Eileen(200.0F, 0.0F),
                }));
        BaseMod.addMonster(Pluto.ID, "Pluto", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Hokma(-550.0F, 0.0F),
                        new Pluto(200.0F, 0.0F),
                }));

        uninvitedGuests.addBoss(Argalia.ID, () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Roland(-500.0F, 0.0F),
                        new Argalia(0.0F, 0.0F),
                }), makeMonsterPath("Argalia/Blue.png"), makeMonsterPath("Argalia/BlueOutline.png"));

        uninvitedGuestsShort.addBoss(Argalia.ID, () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Roland(-500.0F, 0.0F),
                        new Argalia(0.0F, 0.0F),
                }), makeMonsterPath("Argalia/Blue.png"), makeMonsterPath("Argalia/BlueOutline.png"));

        //Black Silence
        BaseMod.addMonster(BlackSilence1.ID, (BaseMod.GetMonster) BlackSilence1::new);
        BaseMod.addMonster(BlackSilence3.ID, "BS3", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new BlackSilence3(),
                        new Angelica()

                }));
        silence.addBoss(BlackSilence4.ID, (BaseMod.GetMonster) BlackSilence4::new, makeMonsterPath("BlackSilence4/BlackSilenceMap.png"), makeMonsterPath("BlackSilence4/BlackSilenceMapOutline.png"));

        //The Head
        BaseMod.addMonster(Baral.ID, "The Head", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new Baral(-250.0F, 0.0F),
                        new Zena(-50.0F, 0.0F),
                }));

        reverbClear = ruinaConfig.getBool("reverbClear");
        blacksilenceClear = ruinaConfig.getBool("blacksilenceClear");
        headClear = ruinaConfig.getBool("headClear");
    }

    public void receiveEditPotions() {
        logger.info("Beginning to edit potions");

        BaseMod.addPotion(EgoPotion.class, EGO_POTION_LIQUID, EGO_POTION_HYBRID, null, EgoPotion.POTION_ID);

        logger.info("Done editing potions");
    }

    private static String makeLocPath(Settings.GameLanguage language, String filename) {
        String ret = "localization/";
        switch (language) {
            case ZHS:
                ret += "zhs/";
                break;
            case RUS:
                ret += "rus/";
                break;
            default:
                ret += "eng/";
                break;
        }
        return getModID() + "Resources/" + (ret + filename + ".json");
    }

    private void loadLocFiles(Settings.GameLanguage language) {
        BaseMod.loadCustomStringsFile(CardStrings.class, makeLocPath(language, "Cardstrings"));
        BaseMod.loadCustomStringsFile(EventStrings.class, makeLocPath(language, "Eventstrings"));
        BaseMod.loadCustomStringsFile(MonsterStrings.class, makeLocPath(language, "Monsterstrings"));
        BaseMod.loadCustomStringsFile(RelicStrings.class, makeLocPath(language, "Relicstrings"));
        BaseMod.loadCustomStringsFile(PowerStrings.class, makeLocPath(language, "Powerstrings"));
        BaseMod.loadCustomStringsFile(UIStrings.class, makeLocPath(language, "UIstrings"));
        BaseMod.loadCustomStringsFile(TutorialStrings.class, makeLocPath(language, "Tutorialstrings"));
        BaseMod.loadCustomStringsFile(PotionStrings.class, makeLocPath(language, "Potionstrings"));
    }

    @Override
    public void receiveEditStrings() {
        loadLocFiles(Settings.GameLanguage.ENG);
        if (Settings.language != Settings.GameLanguage.ENG) {
            loadLocFiles(Settings.language);
        }
    }

    private void loadLocKeywords(Settings.GameLanguage language) {
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
        PlayerSpireFields.totalBlockGained.set(adp(), 0);
        PlayerSpireFields.appliedDebuffThisTurn.set(adp(), false);
    }

    @Override
    public boolean receivePreMonsterTurn(AbstractMonster abstractMonster) {
        PlayerSpireFields.appliedDebuffThisTurn.set(adp(), false);
        if (abstractMonster.hasPower(BadWolf.SKULK_POWER_ID)) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    abstractMonster.halfDead = false;
                    this.isDone = true;
                }
            });
        }
        return !abstractMonster.hasPower(Hokma.POWER_ID);
    }

    @Override
    public void receivePostPowerApplySubscriber(AbstractPower p, AbstractCreature target, AbstractCreature source) {
        if (source == AbstractDungeon.player && target != AbstractDungeon.player && !target.hasPower(ArtifactPower.POWER_ID)) {
            if (p.type == AbstractPower.PowerType.DEBUFF) {
                PlayerSpireFields.appliedDebuffThisTurn.set(adp(), true);
            }
        }
    }

    @Override
    public void receiveStartAct() {
        //Set Neow strings based on the act
        if (CardCrawlGame.dungeon instanceof AbstractRuinaDungeon) {
            EventStrings neowAngelaStrings = CardCrawlGame.languagePack.getEventString(NeowAngela.ID);
            ReflectionHacks.setPrivateStaticFinal(NeowEvent.class, "TEXT", neowAngelaStrings.DESCRIPTIONS);
            ReflectionHacks.setPrivateStaticFinal(NeowEvent.class, "DIALOG_X", 1200.0F * Settings.xScale);
        } else {
            CharacterStrings neowNormalStrings = CardCrawlGame.languagePack.getCharacterString("Neow Event");
            ReflectionHacks.setPrivateStaticFinal(NeowEvent.class, "TEXT", neowNormalStrings.TEXT);
            ReflectionHacks.setPrivateStaticFinal(NeowEvent.class, "DIALOG_X", 1100.0F * Settings.xScale);
        }
    }

    public void receiveEditKeywords() {
        loadLocKeywords(Settings.GameLanguage.ENG);
        if (Settings.language != Settings.GameLanguage.ENG) {
            loadLocKeywords(Settings.language);
        }
    }

    public static void saveConfig() {
        try {
            ruinaConfig.setBool("reverbClear", reverbClear);
            ruinaConfig.setBool("blacksilenceClear", blacksilenceClear);
            ruinaConfig.setBool("headClear", headClear);
            LocalDate clownCheck = LocalDate.now();
            clown = clownCheck.getDayOfMonth() == 1 && clownCheck.getMonth() == Month.APRIL;
            ruinaConfig.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig(){
        reverbClear = ruinaConfig.getBool("reverbClear");
        blacksilenceClear = ruinaConfig.getBool("blacksilenceClear");
        headClear = ruinaConfig.getBool("headClear");
        LocalDate clownCheck = LocalDate.now();
        clown = clownCheck.getDayOfMonth() == 1 && clownCheck.getMonth() == Month.APRIL;
    }

    public static boolean hijackMenu() { return headClear; }
    public static boolean clownTime() { return clown; }
}
