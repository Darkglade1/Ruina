package ruina.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import ruina.RuinaMod;
import ruina.dungeons.AbstractRuinaDungeon;
import ruina.monsters.act1.AllAroundHelper;
import ruina.monsters.act1.Alriune;
import ruina.monsters.act1.Butterflies;
import ruina.monsters.act1.CrazedEmployee;
import ruina.monsters.act1.ForsakenMurderer;
import ruina.monsters.act1.Fragment;
import ruina.monsters.act1.Funeral;
import ruina.monsters.act1.GalaxyFriend;
import ruina.monsters.act1.Orchestra;
import ruina.monsters.act1.Porccubus;
import ruina.monsters.act1.blackSwan.BlackSwan;
import ruina.monsters.act1.blackSwan.Brother;
import ruina.monsters.act1.queenBee.QueenBee;
import ruina.monsters.act1.queenBee.WorkerBee;
import ruina.monsters.act1.redShoes.LeftShoe;
import ruina.monsters.act1.redShoes.RightShoe;
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
import ruina.monsters.act3.blueStar.BlueStar;
import ruina.monsters.act3.blueStar.Worshipper;
import ruina.monsters.act3.heart.HeartOfAspiration;
import ruina.monsters.act3.heart.LungsOfCraving;
import ruina.monsters.act3.priceOfSilence.PriceOfSilence;
import ruina.monsters.act3.priceOfSilence.RemnantOfTime;
import ruina.monsters.act3.punishingBird.PunishingBird;
import ruina.monsters.act3.seraphim.Prophet;
import ruina.monsters.act3.seraphim.Seraphim;
import ruina.monsters.act3.silentGirl.SilentGirl;
import ruina.monsters.blackSilence.blackSilence3.Angelica;
import ruina.monsters.blackSilence.blackSilence3.BlackSilence3;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;
import ruina.monsters.eventboss.yan.monster.yanDistortion;
import ruina.monsters.theHead.Zena;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Argalia;
import ruina.monsters.uninvitedGuests.normal.bremen.Bremen;
import ruina.monsters.uninvitedGuests.normal.clown.Oswald;
import ruina.monsters.uninvitedGuests.normal.eileen.Eileen;
import ruina.monsters.uninvitedGuests.normal.elena.Elena;
import ruina.monsters.uninvitedGuests.normal.greta.Greta;
import ruina.monsters.uninvitedGuests.normal.philip.Philip;
import ruina.monsters.uninvitedGuests.normal.pluto.monster.Pluto;
import ruina.monsters.uninvitedGuests.normal.puppeteer.Puppeteer;
import ruina.monsters.uninvitedGuests.normal.tanya.Tanya;

public class RuinaScene extends AbstractScene {

    private TextureAtlas.AtlasRegion bg;
    private TextureAtlas.AtlasRegion campfireBg;

    public RuinaScene() {
        super(RuinaMod.makeImagePath("scene/atlas.atlas"));

        this.bg = this.atlas.findRegion("mod/Gebura");
        this.campfireBg = this.atlas.findRegion("mod/GeburaCamp");

        this.ambianceName = "AMBIANCE_CITY";
        this.fadeInAmbiance();
    }

    @Override
    public void randomizeScene() {
    }

    @Override
    public void nextRoom(AbstractRoom room) {
        super.nextRoom(room);
        if (room instanceof MonsterRoomBoss) {
            CardCrawlGame.music.silenceBGM();
        }
        if (room.monsters != null) {
            for (AbstractMonster mo : room.monsters.monsters) {
                if (mo instanceof LittleRed) {
                    LittleRed red = (LittleRed)mo;
                    if (red.isDead || red.isDying || red.enraged) {
                        this.bg = this.atlas.findRegion("mod/RedNightForest");
                    } else {
                        this.bg = this.atlas.findRegion("mod/NightForest");
                    }
                    break;
                } else if (mo instanceof Mountain) {
                    this.bg = this.atlas.findRegion("mod/Bodies");
                } else if (mo instanceof Scarecrow) {
                    this.bg = this.atlas.findRegion("mod/Field");
                } else if (mo instanceof Woodsman) {
                    this.bg = this.atlas.findRegion("mod/HeartForest");
                } else if (mo instanceof JesterOfNihil) {
                    this.bg = this.atlas.findRegion("mod/Nihil");
                } else if (mo instanceof SanguineBat || mo instanceof Nosferatu) {
                    this.bg = this.atlas.findRegion("mod/Castle");
                } else if (mo instanceof RoadHome || mo instanceof ScaredyCat) {
                    this.bg = this.atlas.findRegion("mod/Road");
                } else if (mo instanceof KnightOfDespair) {
                    this.bg = this.atlas.findRegion("mod/Despair");
                } else if (mo instanceof KingOfGreed) {
                    this.bg = this.atlas.findRegion("mod/GoldPalace");
                } else if (mo instanceof BadWolf) {
                    this.bg = this.atlas.findRegion("mod/BloodMoon");
                } else if (mo instanceof QueenOfHate) {
                    this.bg = this.atlas.findRegion("mod/Hate");
                } else if (mo instanceof Ozma) {
                    this.bg = this.atlas.findRegion("mod/Crystal");
                } else if (mo instanceof ServantOfWrath || mo instanceof Hermit) {
                    this.bg = this.atlas.findRegion("mod/Wrath");
                } else if (mo instanceof Prophet || mo instanceof Seraphim) {
                    this.bg = this.atlas.findRegion("mod/Paradise");
                } else if (mo instanceof Twilight) {
                    this.bg = this.atlas.findRegion("mod/Twilight");
                } else if (mo instanceof BigBird || mo instanceof PunishingBird || mo instanceof JudgementBird || mo instanceof EyeballChick || mo instanceof RunawayBird) {
                    this.bg = this.atlas.findRegion("mod/BlackForest");
                } else if (mo instanceof BlueStar || mo instanceof Worshipper) {
                    this.bg = this.atlas.findRegion("mod/Star");
                } else if (mo instanceof BurrowingHeaven) {
                    this.bg = this.atlas.findRegion("mod/Heaven");
                } else if (mo instanceof PriceOfSilence || mo instanceof RemnantOfTime) {
                    this.bg = this.atlas.findRegion("mod/Silence");
                } else if (mo instanceof Bloodbath) {
                    this.bg = this.atlas.findRegion("mod/Bloodbath");
                } else if (mo instanceof SnowQueen) {
                    this.bg = this.atlas.findRegion("mod/Snow");
                } else if (mo instanceof HeartOfAspiration || mo instanceof LungsOfCraving) {
                    this.bg = this.atlas.findRegion("mod/Heart");
                } else if (mo instanceof Pinocchio) {
                    this.bg = this.atlas.findRegion("mod/Lies");
                } else if (mo instanceof yanDistortion) {
                    this.bg = this.atlas.findRegion("mod/Yan");
                } else if (mo instanceof Puppeteer) {
                    this.bg = this.atlas.findRegion("mod/Chesed");
                } else if (mo instanceof Argalia) {
                    this.bg = this.atlas.findRegion("mod/Keter");
                } else if (mo instanceof Tanya) {
                    this.bg = this.atlas.findRegion("mod/Gebura");
                } else if (mo instanceof Elena) {
                    this.bg = this.atlas.findRegion("mod/Binah");
                } else if (mo instanceof Pluto) {
                    this.bg = this.atlas.findRegion("mod/Hokma");
                } else if (mo instanceof Oswald) {
                    this.bg = this.atlas.findRegion("mod/Tiph");
                } else if (mo instanceof Bremen) {
                    this.bg = this.atlas.findRegion("mod/Netzach");
                } else if (mo instanceof Philip) {
                    this.bg = this.atlas.findRegion("mod/Malkuth");
                } else if (mo instanceof Greta) {
                    this.bg = this.atlas.findRegion("mod/Hod");
                } else if (mo instanceof Eileen) {
                    this.bg = this.atlas.findRegion("mod/Yesod");
                } else if (mo instanceof BlackSilence4) {
                    this.bg = this.atlas.findRegion("mod/BlackSilence4");
                } else if (mo instanceof BlackSilence3 || mo instanceof Angelica) {
                    this.bg = this.atlas.findRegion("mod/BlackSilence3");
                } else if (mo instanceof Zena && ((Zena) mo).usedShockwave) {
                    this.bg = this.atlas.findRegion("mod/Crater");
                } else if (mo instanceof SilentGirl) {
                    this.bg = this.atlas.findRegion("mod/SilentGirl");
                } else if (mo instanceof AllAroundHelper) {
                    this.bg = this.atlas.findRegion("mod/Helper");
                } else if (mo instanceof ForsakenMurderer) {
                    this.bg = this.atlas.findRegion("mod/Murderer");
                } else if (mo instanceof ScorchedGirl) {
                    this.bg = this.atlas.findRegion("mod/ScorchedGirl");
                } else if (mo instanceof TeddyBear) {
                    this.bg = this.atlas.findRegion("mod/TeddyBear");
                } else if (mo instanceof ShyLook) {
                    this.bg = this.atlas.findRegion("mod/ShyLook");
                } else if (mo instanceof Alriune) {
                    this.bg = this.atlas.findRegion("mod/Alriune");
                } else if (mo instanceof Orchestra) {
                    this.bg = this.atlas.findRegion("mod/Orchestra");
                } else if (mo instanceof FairyQueen) {
                    this.bg = this.atlas.findRegion("mod/Fairy");
                } else if(mo instanceof Laetitia){
                    this.bg = this.atlas.findRegion("mod/Laetitia");
                } else if (mo instanceof Fragment) {
                    this.bg = this.atlas.findRegion("mod/Fragment");
                } else if (mo instanceof CrazedEmployee) {
                    this.bg = this.atlas.findRegion("mod/Singing");
                } else if (mo instanceof Butterflies || mo instanceof Funeral) {
                    this.bg = this.atlas.findRegion("mod/Butterfly");
                } else if (mo instanceof Porccubus) {
                    this.bg = this.atlas.findRegion("mod/Porccubus");
                } else if (mo instanceof LeftShoe || mo instanceof RightShoe) {
                    this.bg = this.atlas.findRegion("mod/RedShoes");
                } else if (mo instanceof GalaxyFriend) {
                    this.bg = this.atlas.findRegion("mod/Galaxy");
                } else if (mo instanceof SpiderBud || mo instanceof Spiderling) {
                    this.bg = this.atlas.findRegion("mod/Spider");
                } else if (mo instanceof QueenBee || mo instanceof WorkerBee) {
                    this.bg = this.atlas.findRegion("mod/QueenBee");
                } else if (mo instanceof BlackSwan || mo instanceof Brother) {
                    this.bg = this.atlas.findRegion("mod/Swan");
                } else {
                    setBgs();
                }
            }
        } else {
            setBgs();
        }
        this.fadeInAmbiance();
    }

    private void setBgs() {
        if (CardCrawlGame.dungeon  instanceof AbstractRuinaDungeon) {
            AbstractRuinaDungeon.Floor floor = ((AbstractRuinaDungeon) CardCrawlGame.dungeon).floor;
            switch (floor) {
                case MALKUTH:
                    this.bg = this.atlas.findRegion("mod/Malkuth");
                    this.campfireBg = this.atlas.findRegion("mod/MalkuthCamp");
                    break;
                case YESOD:
                    this.bg = this.atlas.findRegion("mod/Yesod");
                    this.campfireBg = this.atlas.findRegion("mod/YesodCamp");
                    break;
                case HOD:
                    this.bg = this.atlas.findRegion("mod/Hod");
                    this.campfireBg = this.atlas.findRegion("mod/HodCamp");
                    break;
                case NETZACH:
                    this.bg = this.atlas.findRegion("mod/Netzach");
                    this.campfireBg = this.atlas.findRegion("mod/NetzachCamp");
                    break;
                case TIPHERETH:
                    this.bg = this.atlas.findRegion("mod/Tiph");
                    this.campfireBg = this.atlas.findRegion("mod/TiphCamp");
                    break;
                case GEBURA:
                    this.bg = this.atlas.findRegion("mod/Gebura");
                    this.campfireBg = this.atlas.findRegion("mod/GeburaCamp");
                    break;
                case CHESED:
                    this.bg = this.atlas.findRegion("mod/Chesed");
                    this.campfireBg = this.atlas.findRegion("mod/ChesedCamp");
                    break;
                case BINAH:
                    this.bg = this.atlas.findRegion("mod/Binah");
                    this.campfireBg = this.atlas.findRegion("mod/BinahCamp");
                    break;
                case HOKMA:
                    this.bg = this.atlas.findRegion("mod/Hokma");
                    this.campfireBg = this.atlas.findRegion("mod/HokmaCamp");
                    break;
                case ROLAND:
                    this.bg = this.atlas.findRegion("mod/Keter");
                    this.campfireBg = this.atlas.findRegion("mod/Entrance");
                    break;
                case GUESTS:
                    this.bg = this.atlas.findRegion("mod/Entrance");
                    this.campfireBg = this.atlas.findRegion("mod/Entrance");
                    break;
                case BLACK_SILENCE:
                    this.bg = this.atlas.findRegion("mod/Keter");
                    this.campfireBg = this.atlas.findRegion("mod/Entrance");
                    break;
                default:
                    this.bg = this.atlas.findRegion("mod/Gebura");
                    this.campfireBg = this.atlas.findRegion("mod/GeburaCamp");
                    break;
            }
        }
    }

    @Override
    public void renderCombatRoomBg(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        this.renderAtlasRegionIf(sb, bg, true);
    }

    @Override
    public void renderCombatRoomFg(SpriteBatch sb) {
    }

    @Override
    public void renderCampfireRoom(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        this.renderAtlasRegionIf(sb, this.campfireBg, true);
    }
}