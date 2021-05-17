package ruina.monsters.theHead.dialogue;

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
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.RuinaMod;
import ruina.util.TexLoader;

public class HeadEnding extends AbstractGameEffect {
    public static final String ID = HeadEnding.class.getSimpleName();
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(RuinaMod.makeID(ID));
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    private final TextureRegion SCENE1;
    private final TextureRegion SCENE2;
    private final TextureRegion TEXTBOX;
    private final Dialog roomEventText = new Dialog();
    private int dialogue;
    private Hitbox hb;
    private boolean show;
    private int end;

    public HeadEnding(int start, int end) {
        Texture SCENE1_TEXTURE = TexLoader.getTexture(RuinaMod.makeUIPath("Ending1.png"));
        SCENE1 = new TextureRegion(SCENE1_TEXTURE);
        Texture SCENE2_TEXTURE = TexLoader.getTexture(RuinaMod.makeUIPath("Ending2.png"));
        SCENE2 = new TextureRegion(SCENE2_TEXTURE);
        Texture TEXTBOX_TEXTURE = TexLoader.getTexture(RuinaMod.makeUIPath("Textbox.png"));
        TEXTBOX = new TextureRegion(TEXTBOX_TEXTURE);
        this.dialogue = start;
        this.end = end;
        this.hb = new Hitbox(Settings.WIDTH, Settings.HEIGHT);
        this.hb.x = 0.0F;
        this.hb.y = 0.0F;
        this.show = true;
    }

    public void update() {
        if (this.show) {
            this.show = false;
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
            if(dialogue == 3){ CardCrawlGame.fadeIn(1.5f); }
            this.roomEventText.updateBodyText(DESCRIPTIONS[this.dialogue]);
        } else {
            AbstractDungeon.isScreenUp = false;
            this.isDone = true;
            exit();
        }
    }

    private void exit() {
        AbstractDungeon.screen = AbstractDungeon.CurrentScreen.VICTORY;
        AbstractDungeon.victoryScreen = new VictoryScreen(null);
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        if (dialogue >= 3) {
            sb.draw(SCENE2, 0.0F, 0.0F, 0.0f, 0.0f, SCENE2.getRegionWidth(), SCENE2.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
        } else {
            sb.draw(SCENE1, 0.0F, 0.0F, 0.0f, 0.0f, SCENE1.getRegionWidth(), SCENE1.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
        }
        sb.draw(TEXTBOX, Dialog.DIALOG_MSG_X - (100.0f * Settings.scale), (-170.0f * Settings.scale), 0.0f, 0.0f, TEXTBOX.getRegionWidth(), TEXTBOX.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
        this.roomEventText.render(sb);
    }

    public void dispose() {
        this.roomEventText.clear();
    }

}