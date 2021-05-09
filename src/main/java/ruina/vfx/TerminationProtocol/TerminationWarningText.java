package ruina.vfx.TerminationProtocol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;

public class TerminationWarningText extends AbstractGameEffect {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("RabbitTeam"));
    public static final String[] TEXT = uiStrings.TEXT;
    private GlyphLayout gl = new GlyphLayout();
    private Color color2;
    private boolean fast;
    private float bV, v;
    private float bX, bX2;
    private String word;
    private ArrayList<Float> x = new ArrayList<>(), x2 = new ArrayList<>();
    private float y, y2;

    public TerminationWarningText(){
        this.duration = 1.8F;
        this.startingDuration = this.duration;
        this.color = Color.BLACK.cpy();
        this.color.a = 0.76F;
        this.color2 = new Color(1, 0.21F, 0, 0.76F);
        this.fast = true;
        this.bV = 0;
        this.v = 0;
        this.bX = -Settings.WIDTH;
        this.bX2 = Settings.WIDTH;
        this.word = TEXT[2];
        this.gl.setText(FontHelper.cardTitleFont, this.word);
        this.x.add(-this.gl.width);
        this.x2.add((float) Settings.WIDTH);
        this.y = Settings.HEIGHT * 0.3625F + this.gl.height / 2.0F;
        this.y2 = Settings.HEIGHT * 0.6375F + this.gl.height / 2.0F;
    }

    public void update(){
        this.duration -= Gdx.graphics.getDeltaTime();
        if(this.duration <= 0){ this.isDone = true; }
        if(this.fast){
            this.bV += 100.0F * Gdx.graphics.getDeltaTime();
            this.v = this.bV * this.bV;
            this.bX += Gdx.graphics.getDeltaTime() * this.v;
            this.bX2 -= Gdx.graphics.getDeltaTime() * this.v;
        }
        else {
            this.bV -= 80.0F * Gdx.graphics.getDeltaTime();
            this.v = this.bV * this.bV;
        }
        if(this.bX > 0){
            this.fast = false;
            this.bX = 0;
        }
        if(this.bX2 < 0){
            this.bX2 = 0;
        }
        for(int i = 0; i < this.x.size(); i ++){ this.x.set(i, this.x.get(i) + Gdx.graphics.getDeltaTime() * this.v); }
        if(this.x.get(0) > Settings.WIDTH){ this.x.remove(0); }
        if(this.x.get(this.x.size() - 1) > 0){ this.x.add(this.x.get(this.x.size() - 1)-this.gl.width * 1.15F); }
        for(int i = 0; i < this.x2.size(); i ++){ this.x2.set(i, this.x2.get(i) - Gdx.graphics.getDeltaTime() * this.v); }
        if(this.x2.get(0) + this.gl.width < 0){ this.x2.remove(0); }
        if(this.x2.get(this.x2.size() - 1) + this.gl.width < Settings.WIDTH){ this.x2.add(this.x2.get(this.x2.size() - 1)  + this.gl.width * 1.15F); }
        if(this.duration < 0.4F){
            this.color.a = this.duration * 1.9F;
            this.color2.a = this.color.a;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, this.bX, Settings.HEIGHT * 0.335F, Settings.WIDTH, Settings.HEIGHT * 0.055F);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, this.bX2, Settings.HEIGHT * 0.61F, Settings.WIDTH, Settings.HEIGHT * 0.055F);

        for (Float aX : this.x) { FontHelper.renderSmartText(sb, FontHelper.cardTitleFont, this.word, aX, this.y, Settings.WIDTH, 0.0F, this.color2); }
        for (Float aX : this.x2) { FontHelper.renderSmartText(sb, FontHelper.cardTitleFont, this.word, aX, this.y2, Settings.WIDTH, 0.0F, this.color2); }
    }

    public void dispose(){

    }
}