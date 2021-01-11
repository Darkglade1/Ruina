package ruina.monsters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.vfx.BobEffect;

public class DummyOrb extends AbstractOrb {
    public BobEffect effect;

    public DummyOrb() {
        effect = new BobEffect();
    }

    @Override
    public void update() {
        effect.update();
    }

    @Override
    public void updateDescription() {

    }

    @Override
    public void onEvoke() {

    }

    @Override
    public AbstractOrb makeCopy() {
        return null;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {

    }

    @Override
    public void playChannelSFX() {

    }
}