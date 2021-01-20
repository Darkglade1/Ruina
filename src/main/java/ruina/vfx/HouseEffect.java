package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.RuinaMod;

public class HouseEffect extends AbstractGameEffect {
    public static final String HOUSE = RuinaMod.makeMonsterPath("RoadHome/House.png");
    private static final Texture HOUSE_TEXTURE = new Texture(HOUSE);
    private TextureRegion HOUSE_REGION;

    private static final float DURATION = 4.0f;

    private float y;
    private float offset;
    private float start = 2149.0F;
    private float end = -300.0F;

    private float graphicsAnimation;
    private float scaleWidth;
    private float scaleHeight;

    public HouseEffect() {
        this.HOUSE_REGION = new TextureRegion(HOUSE_TEXTURE);
        this.y = AbstractDungeon.floorY + 100.0F * Settings.scale;
        this.color = Color.WHITE.cpy();
        this.color.a = 0.0F;
        this.duration = this.startingDuration = DURATION;
        this.graphicsAnimation = 0.0F;
        this.offset = start;

        this.scaleWidth = 1.0F * Settings.scale;
        this.scaleHeight = Settings.scale;
    }


    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.graphicsAnimation += Gdx.graphics.getDeltaTime();
        if (this.graphicsAnimation <= 0.5F) {

            this.color.a = this.graphicsAnimation * DURATION;
            if (this.color.a > 1.0F) {
                this.color.a = 1.0F;
            }
        }
        if (this.graphicsAnimation <= DURATION) {
            this.offset = Interpolation.linear.apply(start, end, this.graphicsAnimation * 1.5F);
        }
        if (this.duration <= 0.0F) {
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.draw(this.HOUSE_REGION, (this.offset + this.HOUSE_REGION.getRegionWidth()) * this.scaleWidth, y - (this.HOUSE_REGION.getRegionHeight() * this.scaleHeight) / 2, 0.0F, 0.0F, this.HOUSE_REGION.getRegionWidth(), this.HOUSE_REGION.getRegionHeight(), this.scaleWidth, this.scaleHeight, 0.0F);
        sb.draw(this.HOUSE_REGION, (this.offset + this.HOUSE_REGION.getRegionWidth() * 2) * this.scaleWidth, y - (this.HOUSE_REGION.getRegionHeight() * this.scaleHeight) / 2, 0.0F, 0.0F, this.HOUSE_REGION.getRegionWidth(), this.HOUSE_REGION.getRegionHeight(), this.scaleWidth, this.scaleHeight, 0.0F);
        sb.draw(this.HOUSE_REGION, (this.offset + this.HOUSE_REGION.getRegionWidth() * 3) * this.scaleWidth, y - (this.HOUSE_REGION.getRegionHeight() * this.scaleHeight) / 2, 0.0F, 0.0F, this.HOUSE_REGION.getRegionWidth(), this.HOUSE_REGION.getRegionHeight(), this.scaleWidth, this.scaleHeight, 0.0F);
        sb.draw(this.HOUSE_REGION, (this.offset + this.HOUSE_REGION.getRegionWidth() * 4) * this.scaleWidth, y - (this.HOUSE_REGION.getRegionHeight() * this.scaleHeight) / 2, 0.0F, 0.0F, this.HOUSE_REGION.getRegionWidth(), this.HOUSE_REGION.getRegionHeight(), this.scaleWidth, this.scaleHeight, 0.0F);
    }

    public void dispose() {
    }
}


