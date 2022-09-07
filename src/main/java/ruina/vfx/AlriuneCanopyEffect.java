package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.RuinaMod;
import ruina.util.TexLoader;

public class AlriuneCanopyEffect extends AbstractGameEffect {
    public static final String CANOPY = RuinaMod.makeVfxPath("Alriune_Canopy.png");
    public static final String FLOWER_LEFT = RuinaMod.makeVfxPath("Alriune_Flower_L.png");
    public static final String FLOWER_RIGHT = RuinaMod.makeVfxPath("Alriune_Flower_R.png");

    private final Texture CANOPY_TEXTURE = TexLoader.getTexture(CANOPY);
    private final Texture FLOWER_LEFT_1 = TexLoader.getTexture(FLOWER_LEFT);
    private final Texture FLOWER_LEFT_2 = TexLoader.getTexture(FLOWER_LEFT);
    private final Texture FLOWER_RIGHT_1 = TexLoader.getTexture(FLOWER_RIGHT);
    private final Texture FLOWER_RIGHT_2 = TexLoader.getTexture(FLOWER_RIGHT);

    private float x;
    private float x2;
    private float y;

    public AlriuneCanopyEffect() {
        this.duration = 4.2F;
        x = -FLOWER_LEFT_1.getWidth() * Settings.scale * 0.5f;
        y = Settings.HEIGHT - FLOWER_LEFT_1.getHeight() * Settings.scale;
        x2 = x;
        scale = 1f;
        color = Color.WHITE.cpy();
        color.a = 0f;
    }

    public void update() {
        duration -= Gdx.graphics.getDeltaTime();
        if (duration < 4.0F) {
            if (duration > 3.5F) { color.a = (4F - duration) * 2F; }
            else if (duration > 2.5F) { color.a = 1.0F; }
            else if (duration > 2.0F) {
                x += FLOWER_LEFT_1.getWidth() * Settings.scale * Gdx.graphics.getDeltaTime();
                x2 = x;
            }
            else if (duration > 1.5F) {
                x = 0.0F;
                x2 += FLOWER_LEFT_1.getWidth() * Settings.scale * Gdx.graphics.getDeltaTime() * 2.0F;
            }
            else if (duration > 1.0F) {
                x2 = FLOWER_LEFT_1.getWidth() * Settings.scale;
            }
            else if (this.duration < 0.5F) {
                color.a = duration * 2.0F;
                x -= FLOWER_LEFT_1.getWidth() * Settings.scale * Gdx.graphics.getDeltaTime();
                x2 = x;
            } else {
                x2 -= FLOWER_LEFT_1.getWidth() * Settings.scale * Gdx.graphics.getDeltaTime() * 2.0F;
            }
        }
        if(duration <= 0){ isDone = true; }
    }
    public void render(SpriteBatch sb) {
        sb.setColor(color);
        sb.draw(FLOWER_RIGHT_2, Settings.WIDTH - x - FLOWER_RIGHT_2.getWidth(), y, FLOWER_RIGHT_2.getWidth() * Settings.scale, FLOWER_RIGHT_2.getHeight() * Settings.scale);
        sb.draw(FLOWER_LEFT_2, x, y, FLOWER_LEFT_2.getWidth() * Settings.scale, FLOWER_LEFT_2.getHeight() * Settings.scale);
        sb.draw(FLOWER_RIGHT_1, Settings.WIDTH - x2 - FLOWER_RIGHT_1.getWidth(), y, FLOWER_RIGHT_1.getWidth() * Settings.scale, FLOWER_RIGHT_1.getHeight() * Settings.scale);
        sb.draw(FLOWER_LEFT_1, x2, y, FLOWER_LEFT_1.getWidth() * Settings.scale, FLOWER_LEFT_1.getHeight() * Settings.scale);
        sb.draw(CANOPY_TEXTURE, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
    }

    public void dispose() {}
}


