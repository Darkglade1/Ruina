package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

//Because I'm too lazy to patch wait action
public class WaitEffect
        extends AbstractGameEffect {

    private static final float DURATION = 0.0f;

    public WaitEffect() {
        this.duration = this.startingDuration = DURATION;
    }


    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration <= 0.0F) {
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {

    }

    public void dispose() {
    }
}


