package ruina.monsters.day49.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import ruina.RuinaMod;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Fields.D49SaveData;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.TreeOfLifeManager;
import ruina.monsters.theHead.Baral;
import ruina.util.TexLoader;

import java.util.Random;

import static ruina.util.Wiz.adp;

public class FloorOption {

    private FloorENUM floor;
    private FloorTYPE region;

    private Hitbox hb;
    private float HB_W = 90f * Settings.scale;
    private float HB_H = 90f * Settings.scale;

    private Texture floorTexture;
    public float buttonXPosition;
    public float buttonYPosition;

    private boolean lockedOption = false;
    private boolean completed = false;

    public static final String OUTLINE_INTENSE = RuinaMod.makeVfxPath("outline_intense.png");

    private final Texture OUTLINE_INTENSE_IMG = TexLoader.getTexture(OUTLINE_INTENSE);

    private boolean preventClicking = false;


    public FloorOption(FloorENUM selectedFloor, FloorTYPE type) {
        floor = selectedFloor;
        region = type;
        floorTexture = new Texture(RuinaMod.makeVfxPath(floor.toString().toLowerCase() + ".png"));
        hb = new Hitbox(HB_W, HB_H);
        init();
    }

    public void adjustHB(float x, float y) {
        hb.move(x, y);
    }

    public void init() {
        switch (floor) {
            case MALKUTH:
                buttonXPosition = 225f * Settings.scale;
                buttonYPosition = 99f * Settings.scale;
                if(D49SaveData.Fields.defeatedMalkuth.get(adp())){
                    completed = true;
                    lockedOption = true;
                }
                break;
            case YESOD:
                buttonXPosition = 225f * Settings.scale;
                buttonYPosition = 228.6F * Settings.scale;
                if(D49SaveData.Fields.defeatedYesod.get(adp())){
                    completed = true;
                    lockedOption = true;
                }
                break;
            case HOD:
                buttonXPosition = 104.4F * Settings.scale;
                buttonYPosition = 306F * Settings.scale;
                if(D49SaveData.Fields.defeatedHod.get(adp())){
                    completed = true;
                    lockedOption = true;
                }
                break;
            case NETZACH:
                buttonXPosition = 352F * Settings.scale;
                buttonYPosition = 304.2F * Settings.scale;
                if(D49SaveData.Fields.defeatedNetzach.get(adp())){
                    completed = true;
                    lockedOption = true;
                }
                break;
            case TIPHERETH:
                buttonXPosition = 225f * Settings.scale;
                buttonYPosition = 370.8F * Settings.scale;
                if(D49SaveData.Fields.defeatedTiphereth.get(adp())){
                    completed = true;
                    lockedOption = true;
                }
                break;
            case GEBURA:
                buttonXPosition = 104.4F * Settings.scale;
                buttonYPosition = 441F * Settings.scale;
                if(D49SaveData.Fields.defeatedGebura.get(adp())){
                    completed = true;
                    lockedOption = true;
                }
                break;
            case CHESED:
                buttonXPosition = 352F * Settings.scale;
                buttonYPosition = 441F * Settings.scale;
                if(D49SaveData.Fields.defeatedChesed.get(adp())){
                    completed = true;
                    lockedOption = true;
                }
                break;
            case BINAH:
                buttonXPosition = 104F * Settings.scale;
                buttonYPosition = 561F * Settings.scale;
                if(D49SaveData.Fields.defeatedBinah.get(adp())){
                    completed = true;
                    lockedOption = true;
                }
                break;
            case HOKMA:
                buttonXPosition = 352F * Settings.scale;
                buttonYPosition = 561F * Settings.scale;
                if(D49SaveData.Fields.defeatedHokma.get(adp())){
                    completed = true;
                    lockedOption = true;
                }
                break;
            case KETER:
                buttonXPosition = 225F * Settings.scale;
                buttonYPosition = 630F * Settings.scale;
                break;
        }
        switch (region){
            case BRIAH:
                if(!D49SaveData.Fields.isBriahFloorsOpen.get(adp())){
                    lockedOption = true;
                }
                break;
            case ATZILUTH:
                if(!D49SaveData.Fields.isAtziluthFloorsOpen.get(adp())){
                    lockedOption = true;
                }
                break;
            case FINAL:
                if(!D49SaveData.Fields.isKeterOpen.get(adp())){
                    lockedOption = true;
                }
                break;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        sb.draw(floorTexture, hb.cX - 128.0F, hb.cY - 128.0F, 128.0F, 128.0F, 256.0F, 256.0F, 0.5F * Settings.scale, 0.5F * Settings.scale, 0.0F, 0, 0, 256, 256, false, false);
        if(lockedOption){
            if(completed){ sb.setColor(Color.GREEN); }
            else { sb.setColor(Color.RED); }
        }
        else if (hb.hovered) { sb.setColor(Color.WHITE.cpy());
        } else { sb.setColor(Color.GRAY); }
        sb.draw(OUTLINE_INTENSE_IMG, hb.cX - 50.0F, hb.cY - 50.0F, 50.0F, 50.0F, 100.0F, 100.0F, 1.5F * Settings.scale, 1.5F * Settings.scale, 0.0F, 0, 0, 100, 100, false, false);
    }

    public void update() {
        hb.update();
        if (hb.justHovered) CardCrawlGame.sound.playA("UI_HOVER", -0.3F);
        if (InputHelper.justClickedLeft && !lockedOption && hb.hovered) {
            CardCrawlGame.sound.playA("UI_CLICK_1", -0.4F);
            hb.clickStarted = true;
        }
        if(hb.clicked && !preventClicking){
            preventClicking = true;
            hb.clicked = false;
            goToBossRespectiveOfButton(floor);
        }
    }

    public void goToBossRespectiveOfButton(FloorENUM floor){
            if(AbstractDungeon.bossList.isEmpty()){ AbstractDungeon.bossList.add("Hexaghost"); }
            AbstractDungeon.bossKey = TreeOfLifeManager.ID;
            CardCrawlGame.music.fadeOutBGM();
            CardCrawlGame.music.fadeOutTempBGM();
            MapRoomNode node = new MapRoomNode(-1, 15);
            node.room = new MonsterRoomBoss();
            AbstractDungeon.nextRoom = node;
            AbstractDungeon.closeCurrentScreen();
            AbstractDungeon.nextRoomTransitionStart();
    }
}
