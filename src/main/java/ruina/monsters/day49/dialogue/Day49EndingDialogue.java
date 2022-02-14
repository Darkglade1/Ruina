package ruina.monsters.day49.dialogue;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.RuinaMod;
import ruina.monsters.day49.speaker.*;
import ruina.monsters.theHead.dialogue.AbstractSpeaker;
import ruina.monsters.theHead.dialogue.Dialog;
import ruina.util.TexLoader;

import java.util.ArrayList;

public class Day49EndingDialogue extends AbstractGameEffect {
    public static final String ID = Day49EndingDialogue.class.getSimpleName();
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(RuinaMod.makeID(ID));
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    private final TextureRegion THE_LIGHT;
    private final TextureRegion THE_CITY;
    private final TextureRegion THE_LIBRARY;
    private final TextureRegion THE_ENDING;
    private final TextureRegion TEXTBOX;
    private final Dialog roomEventText = new Dialog();
    private int dialogue;
    private Hitbox hb;
    private boolean show;
    private int end;
    private ArrayList<AbstractSpeaker> characters = new ArrayList<>();
    private int charsOnScreen;

    public Day49EndingDialogue(int start, int end) {
        Texture LIGHT_TEXTURE = TexLoader.getTexture(RuinaMod.makeScenePath("Light.png"));
        THE_LIGHT = new TextureRegion(LIGHT_TEXTURE);
        Texture CITY_TEXTURE = TexLoader.getTexture(RuinaMod.makeUIPath("day49Panel6.png"));
        THE_CITY = new TextureRegion(CITY_TEXTURE);
        Texture LIBRARY_TEXTURE = TexLoader.getTexture(RuinaMod.makeUIPath("LibraryPostDay49.png"));
        THE_LIBRARY = new TextureRegion(LIBRARY_TEXTURE);
        Texture ENDING_TEXTURE = TexLoader.getTexture(RuinaMod.makeUIPath("day49Panel7.png"));
        THE_ENDING = new TextureRegion(ENDING_TEXTURE);
        Texture TEXTBOX_TEXTURE = TexLoader.getTexture(RuinaMod.makeUIPath("Textbox.png"));
        TEXTBOX = new TextureRegion(TEXTBOX_TEXTURE);
        this.dialogue = start;
        this.end = end;
        this.hb = new Hitbox(Settings.WIDTH, Settings.HEIGHT);
        this.hb.x = 0.0F;
        this.hb.y = 0.0F;
        this.show = true;
        characters.add(new ts_Angela_LOB());
        characters.add(new ts_Angela_LOR());
        characters.add(new ts_BloodBath());
        characters.add(new ts_RolandD49());
        characters.add(new ts_Carmen());
    }

    public void update() {
        if (this.show) {
            this.show = false;
            calculateSpeakers();
            AbstractDungeon.isScreenUp = true;
            this.roomEventText.show(DESCRIPTIONS[this.dialogue]);
        }
        this.hb.update();
        if (this.hb.hovered && InputHelper.justClickedLeft) {
            InputHelper.justClickedLeft = false;
            this.hb.clickStarted = true;
        }
        if (this.hb.clicked) {
            this.hb.clicked = false;
            nextDialogue();
        }
        this.roomEventText.update();
    }

    private void nextDialogue() {
        if (this.dialogue < end) {
            this.dialogue++;
            if(dialogue == 9) {
                CardCrawlGame.fadeIn(2f);
                CustomDungeon.playTempMusicInstantly("Sanctuary");
            }
            else if (dialogue == 19) {
                CardCrawlGame.fadeIn(2f);
                CustomDungeon.playTempMusicInstantly("Inspiration");
            }
            else if(dialogue == 32){
                CardCrawlGame.fadeIn(2f);
            }
            calculateSpeakers();
            this.roomEventText.updateBodyText(DESCRIPTIONS[this.dialogue]);
        } else {
            AbstractDungeon.isScreenUp = false;
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        if(dialogue <= 8) {
            sb.draw(THE_LIGHT, 0.0F, 0.0F, 0.0f, 0.0f, THE_LIGHT.getRegionWidth(), THE_LIGHT.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
        }
        else if(dialogue <= 18 ){
            sb.draw(THE_CITY, 0.0F, 0.0F, 0.0f, 0.0f, THE_LIGHT.getRegionWidth(), THE_LIGHT.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
        }
        else if(dialogue <= 31 ){
            sb.draw(THE_LIBRARY, 0.0F, 0.0F, 0.0f, 0.0f, THE_LIGHT.getRegionWidth(), THE_LIGHT.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
        }
        else {
            sb.draw(THE_ENDING, 0.0F, 0.0F, 0.0f, 0.0f, THE_LIGHT.getRegionWidth(), THE_LIGHT.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
        }
        for(AbstractSpeaker s : characters){ s.render(sb, charsOnScreen); }
        sb.draw(TEXTBOX, Dialog.DIALOG_MSG_X - (100.0f * Settings.scale), (-170.0f * Settings.scale), 0.0f, 0.0f, TEXTBOX.getRegionWidth(), TEXTBOX.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
        this.roomEventText.render(sb);
    }

    public void dispose() {
        this.roomEventText.clear();
    }

    public void calculateSpeakers(){
        charsOnScreen = 0;
        String[] speakerData = OPTIONS[dialogue].split("_");
        for(AbstractSpeaker s: characters){
            s.setTexture(speakerData[1]);
            s.setShowBasedOnCharList(speakerData[0]);
            if(s.getShow()){ charsOnScreen += 1; }
        }
    }

}