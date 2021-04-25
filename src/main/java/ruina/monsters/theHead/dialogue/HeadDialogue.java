package ruina.monsters.theHead.dialogue;

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
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;
import ruina.util.TexLoader;

public class HeadDialogue extends AbstractGameEffect {
    public static final String ID = HeadDialogue.class.getSimpleName();
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(RuinaMod.makeID(ID));
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    private final TextureRegion BARAL;
    private final String baralName = CardCrawlGame.languagePack.getMonsterStrings(RuinaMod.makeID(Baral.class.getSimpleName())).NAME;
    private final TextureRegion ZENA;
    private final String zenaName = CardCrawlGame.languagePack.getMonsterStrings(RuinaMod.makeID(Zena.class.getSimpleName())).NAME;
    private final TextureRegion ROLAND;
    private final String rolandName = CardCrawlGame.languagePack.getMonsterStrings(RuinaMod.makeID(Roland.class.getSimpleName())).NAME;
    private final TextureRegion KETER;
    private final RoomEventDialog roomEventText = new RoomEventDialog();
    private int dialogue;
    private Hitbox hb;
    private boolean show;
    private int end;
    private String speaker;

    public HeadDialogue(int start, int end) {
        Texture BARAL_TEXTURE = TexLoader.getTexture(RuinaMod.makeUIPath("Baral.png"));
        BARAL = new TextureRegion(BARAL_TEXTURE);
        Texture ZENA_TEXTURE = TexLoader.getTexture(RuinaMod.makeUIPath("Zena.png"));
        ZENA = new TextureRegion(ZENA_TEXTURE);
        Texture ROLAND_TEXTURE = TexLoader.getTexture(RuinaMod.makeUIPath("Roland.png"));
        ROLAND = new TextureRegion(ROLAND_TEXTURE);
        Texture KETER_TEXTURE = TexLoader.getTexture(RuinaMod.makeScenePath("Keter.png"));
        KETER = new TextureRegion(KETER_TEXTURE);
        this.dialogue = start;
        this.end = end;
        speaker = OPTIONS[dialogue];
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
//        if (this.dialogue == 0) {
//            AbstractDungeon.topLevelEffectsQueue.add(new BorderFlashEffect(Color.YELLOW));
//        }
        if (this.dialogue < end) {
            this.dialogue++;
            speaker = OPTIONS[dialogue];
            this.roomEventText.updateBodyText(DESCRIPTIONS[this.dialogue]);
        } else {
            AbstractDungeon.isScreenUp = false;
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        sb.draw(KETER, 0.0F, 0.0F, 0.0f, 0.0f, KETER.getRegionWidth(), KETER.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
        if (speaker.equals(zenaName)) {
            drawSprite(sb, ZENA);
        } else if (speaker.equals(baralName)) {
            drawSprite(sb, BARAL);
        } else if (speaker.equals(rolandName)) {
            drawSprite(sb, ROLAND);
        }
        this.roomEventText.render(sb);
    }

    public void drawSprite(SpriteBatch sb, TextureRegion texture) {
        sb.draw(texture, Settings.WIDTH * 0.75F - ((float)texture.getRegionWidth() / 2), 0.0f, (float)texture.getRegionWidth() / 2, 0.0f, texture.getRegionWidth(), texture.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
    }

    public void dispose() {
        this.roomEventText.clear();
    }
}