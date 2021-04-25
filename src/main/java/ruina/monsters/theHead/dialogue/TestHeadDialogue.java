package ruina.monsters.theHead.dialogue;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import ruina.RuinaMod;

public class TestHeadDialogue extends AbstractGameEffect {
    public static final String ID = TestHeadDialogue.class.getSimpleName();
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(RuinaMod.makeID(ID));
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private Texture img;
    private Texture img2;
    private RoomEventDialog roomEventText = new RoomEventDialog();
    private int dialogue;
    private Hitbox hb;
    private boolean show;

    public TestHeadDialogue() {
        this.img = new Texture(RuinaMod.makeUIPath("roland.png"));
        this.img2 = new Texture(RuinaMod.makeUIPath("roland2.png"));
        this.dialogue = 0;
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
        if (this.dialogue == 0) {
            AbstractDungeon.topLevelEffectsQueue.add(new BorderFlashEffect(Color.YELLOW));
        }
        if (this.dialogue < DESCRIPTIONS.length - 1) {
            this.dialogue++;
            this.roomEventText.updateBodyText(DESCRIPTIONS[this.dialogue]);
        } else {
            AbstractDungeon.isScreenUp = false;
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.BLACK.cpy());
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
        sb.setColor(Color.WHITE.cpy());
        if (this.dialogue > 0) { sb.draw(this.img2, Settings.WIDTH * 0.75F - (this.img2.getWidth() / 2), Settings.HEIGHT * 0.8F - this.img.getHeight(), this.img2.getWidth(), this.img2.getHeight());
        } else { sb.draw(this.img, Settings.WIDTH * 0.75F - (this.img.getWidth() / 2), Settings.HEIGHT * 0.8F - this.img.getHeight(), this.img.getWidth(), this.img.getHeight()); }
        this.roomEventText.render(sb);
    }

    public void dispose() { this.roomEventText.clear(); }
}