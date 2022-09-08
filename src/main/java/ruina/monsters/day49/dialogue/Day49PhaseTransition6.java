package ruina.monsters.day49.dialogue;

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
import ruina.monsters.day49.speaker.ts_Angela_LOB;
import ruina.monsters.day49.speaker.ts_Angela_LOR;
import ruina.monsters.day49.speaker.ts_BloodBath;
import ruina.monsters.day49.speaker.ts_RolandD49;
import ruina.monsters.theHead.dialogue.AbstractSpeaker;
import ruina.monsters.theHead.dialogue.Dialog;
import ruina.util.TexLoader;

import java.util.ArrayList;

public class Day49PhaseTransition6 extends AbstractGameEffect {
    public static final String ID = Day49PhaseTransition6.class.getSimpleName();
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(RuinaMod.makeID(ID));
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    private TextureRegion ANGELA_BG;

    private final TextureRegion TEXTBOX;
    private final Dialog roomEventText = new Dialog();
    private int dialogue;
    private Hitbox hb;
    private boolean show;
    private int end;
    private ArrayList<AbstractSpeaker> characters = new ArrayList<>();
    private int charsOnScreen;

    public Day49PhaseTransition6(int start, int end) {
        Texture ANGELA_TEXTURE = TexLoader.getTexture(RuinaMod.makeUIPath("day49Panel5b.png"));
        ANGELA_BG = new TextureRegion(ANGELA_TEXTURE);
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
            if(dialogue == 1){ ANGELA_BG = new TextureRegion(TexLoader.getTexture(RuinaMod.makeUIPath("day49Panel5c.png"))); }
            else if(dialogue == 1){ ANGELA_BG = new TextureRegion(TexLoader.getTexture(RuinaMod.makeUIPath("day49Panel5d.png"))); }
            else if(dialogue == 7){ ANGELA_BG = new TextureRegion(TexLoader.getTexture(RuinaMod.makeUIPath("day49Panel5e.png"))); }
            calculateSpeakers();
            this.roomEventText.updateBodyText(DESCRIPTIONS[this.dialogue]);
        } else {
            CardCrawlGame.fadeIn(1.5f);
            AbstractDungeon.isScreenUp = false;
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        sb.draw(ANGELA_BG, 0.0F, 0.0F, 0.0f, 0.0f, ANGELA_BG.getRegionWidth(), ANGELA_BG.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
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