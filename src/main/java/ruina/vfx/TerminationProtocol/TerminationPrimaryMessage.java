package ruina.vfx.TerminationProtocol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import static ruina.RuinaMod.makeID;

public class TerminationPrimaryMessage extends AbstractGameEffect {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("RabbitTeam"));
    public static final String[] TEXT = uiStrings.TEXT;
    private GlyphLayout gl = new GlyphLayout();
    private String word, word2;
    private float x, y, x2;
    private float space;

    public TerminationPrimaryMessage(){
        this.duration = 1.8F;
        this.startingDuration = this.duration;
        this.color = Color.BLACK.cpy();
        this.color.a = 0.9F;

        this.word = TEXT[0];
        this.word2 = TEXT[1];
        this.x = 0.0F;
        this.x2 = 0.0F;
        this.space = Settings.WIDTH / 2.0F;
    }

    public void update(){
        this.duration -= Gdx.graphics.getDeltaTime();
        if(this.duration <= 0){
            this.isDone = true;
        }

        if(this.duration > 0.8F){
            this.space = (this.duration - 0.8F) * Settings.WIDTH / 2.0F * 1.0625F + Settings.WIDTH / 2.0F * 0.15F;
        }
        else {
            this.space = Settings.WIDTH / 2.0F * 0.15F;
        }

        if(this.duration < 0.4F){
            this.color.a = this.duration * 2.25F;
        }

        this.gl.setText(FontHelper.charTitleFont, this.word);
        this.x = Settings.WIDTH / 2.0F - this.gl.width - this.space;
        this.y = Settings.HEIGHT / 2.0F + this.gl.height / 2.0F;

        this.gl.setText(FontHelper.charTitleFont, this.word2);
        this.x2 = Settings.WIDTH / 2.0F + this.space;
    }

    @Override
    public void render(SpriteBatch sb) {
        FontHelper.renderSmartText(sb, FontHelper.charTitleFont, this.word, this.x, this.y, Settings.WIDTH, 0.0F, this.color);
        FontHelper.renderSmartText(sb, FontHelper.charTitleFont, this.word2, this.x2, this.y, Settings.WIDTH, 0.0F, this.color);
    }

    public void dispose(){

    }
}