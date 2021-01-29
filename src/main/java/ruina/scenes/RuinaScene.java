package ruina.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import ruina.monsters.act2.BadWolf;
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
import ruina.monsters.act2.Woodsman;

public class RuinaScene extends AbstractScene {

    private static Texture topBar;
    private TextureAtlas.AtlasRegion bg;
    private TextureAtlas.AtlasRegion fg;
    private TextureAtlas.AtlasRegion ceil;
    private TextureAtlas.AtlasRegion fgGlow;
    private TextureAtlas.AtlasRegion floor;
    private TextureAtlas.AtlasRegion mg1;
    private Texture campfirebg;
    private Texture campfire;
    private Texture fire;

    public RuinaScene() {
        super("ruinaResources/images/scene/atlas.atlas");

        this.bg = this.atlas.findRegion("mod/NightForest");
        //this.fg = this.atlas.findRegion("mod/fg");
        //this.ceil = this.atlas.findRegion("mod/ceiling");
        //this.fgGlow = this.atlas.findRegion("mod/fgGlow");
        //this.floor = this.atlas.findRegion("mod/floor");
        //this.mg1 = this.atlas.findRegion("mod/mg1");

        this.ambianceName = "AMBIANCE_CITY";
        this.fadeInAmbiance();
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void randomizeScene() {
    }

    @Override
    public void nextRoom(AbstractRoom room) {
        super.nextRoom(room);
        this.randomizeScene();
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
                } else {
                    this.bg = this.atlas.findRegion("mod/NightForest");
                }
            }
        } else if (room instanceof ShopRoom) {
            this.bg = this.atlas.findRegion("mod/NightForest");
        } else {
            this.bg = this.atlas.findRegion("mod/NightForest");
        }
        this.fadeInAmbiance();
    }

    @Override
    public void renderCombatRoomBg(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        this.renderAtlasRegionIf(sb, bg, true);
        sb.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
        //this.renderAtlasRegionIf(sb, this.floor, true);
        // this.renderAtlasRegionIf(sb, this.ceil, true);
        //this.renderAtlasRegionIf(sb, this.mg1, true);
    }

    @Override
    public void renderCombatRoomFg(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        sb.setColor(Color.WHITE.cpy());
        // this.renderAtlasRegionIf(sb, this.fg, true);
        // sb.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE);
        // this.renderAtlasRegionIf(sb, this.fgGlow, true);
        // sb.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void renderCampfireRoom(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        this.renderAtlasRegionIf(sb, this.campfireBg, true);
        sb.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE);
        sb.setColor(new Color(1.0f, 1.0f, 1.0f, MathUtils.cosDeg(System.currentTimeMillis() / 3L % 360L) / 10.0f + 0.8f));
        this.renderQuadrupleSize(sb, this.campfireGlow, !CampfireUI.hidden);
        sb.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
        sb.setColor(Color.WHITE);
        this.renderAtlasRegionIf(sb, this.campfireKindling, true);
    }
}