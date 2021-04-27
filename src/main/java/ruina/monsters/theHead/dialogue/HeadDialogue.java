package ruina.monsters.theHead.dialogue;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.RuinaMod;
import ruina.monsters.theHead.Baral;
import ruina.monsters.theHead.Zena;
import ruina.monsters.theHead.dialogue.speaker.*;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;
import ruina.util.TexLoader;

import java.util.ArrayList;

public class HeadDialogue extends AbstractGameEffect {
    public static final String ID = HeadDialogue.class.getSimpleName();
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(RuinaMod.makeID(ID));
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    private final TextureRegion KETER;
    private final RoomEventDialog roomEventText = new RoomEventDialog();
    private int dialogue;
    private Hitbox hb;
    private boolean show;
    private int end;
    private ArrayList<AbstractSpeaker> characters = new ArrayList<>();
    private int charsOnScreen;

    public HeadDialogue(int start, int end) {
        Texture KETER_TEXTURE = TexLoader.getTexture(RuinaMod.makeScenePath("KeterDialogue.png"));
        KETER = new TextureRegion(KETER_TEXTURE);
        this.dialogue = start;
        this.end = end;
        this.hb = new Hitbox(Settings.WIDTH, Settings.HEIGHT);
        this.hb.x = 0.0F;
        this.hb.y = 0.0F;
        this.show = true;
        characters.add(new ts_Baral());
        characters.add(new ts_Zena());
        characters.add(new ts_Roland());
        characters.add(new ts_Gebura());
        characters.add(new ts_Binah());
        ReflectionHacks.setPrivateStaticFinal(roomEventText.getClass(), "DIALOG_MSG_Y", (float) 350f * Settings.scale);
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
//        if (this.dialogue == 0) {
//            AbstractDungeon.topLevelEffectsQueue.add(new BorderFlashEffect(Color.YELLOW));
//        }
        if (this.dialogue < end) {
            this.dialogue++;
            calculateSpeakers();
            this.roomEventText.updateBodyText(DESCRIPTIONS[this.dialogue]);
        } else {
            AbstractDungeon.isScreenUp = false;
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        sb.draw(KETER, 0.0F, 0.0F, 0.0f, 0.0f, KETER.getRegionWidth(), KETER.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
        for(AbstractSpeaker s : characters){ s.render(sb, charsOnScreen); }
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