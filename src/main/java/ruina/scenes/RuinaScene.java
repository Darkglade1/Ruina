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
import ruina.monsters.act2.BadWolf;
import ruina.monsters.act2.Hermit;
import ruina.monsters.act2.Jester.JesterOfNihil;
import ruina.monsters.act2.KingOfGreed;
import ruina.monsters.act2.KnightOfDespair;
import ruina.monsters.act2.LittleRed;
import ruina.monsters.act2.Mountain;
import ruina.monsters.act2.Nosferatu;
import ruina.monsters.act2.Ozma;
import ruina.monsters.act2.QueenOfHate;
import ruina.monsters.act2.RoadHome;
import ruina.monsters.act2.SanguineBat;
import ruina.monsters.act2.Scarecrow;
import ruina.monsters.act2.ScaredyCat;
import ruina.monsters.act2.ServantOfWrath;
import ruina.monsters.act2.Woodsman;
import ruina.monsters.act3.Bloodbath;
import ruina.monsters.act3.BurrowingHeaven;
import ruina.monsters.act3.EyeballChick;
import ruina.monsters.act3.JudgementBird;
import ruina.monsters.act3.Pinocchio;
import ruina.monsters.act3.RunawayBird;
import ruina.monsters.act3.SnowQueen.SnowQueen;
import ruina.monsters.act3.Twilight;
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
import ruina.monsters.eventboss.yan.monster.yanDistortion;
import ruina.monsters.uninvitedGuests.argalia.monster.Argalia;
import ruina.monsters.uninvitedGuests.elena.Elena;
import ruina.monsters.uninvitedGuests.pluto.monster.Pluto;
import ruina.monsters.uninvitedGuests.puppeteer.Puppeteer;
import ruina.monsters.uninvitedGuests.tanya.Tanya;

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