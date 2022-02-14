package ruina.monsters.day49.speaker;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import ruina.monsters.theHead.dialogue.speaker.ts_Roland;

public class ts_RolandD49 extends ts_Roland {

    public ts_RolandD49(){ super(); }

    public void setTexture(String passedKey){
        super.setTexture(passedKey);
        if(!activeSprite.isFlipX()){ activeSprite.flip(true, false); }
    }

    public void drawSprite(SpriteBatch sb, TextureRegion texture, int charsOnScreen) {
        sb.draw(texture, Settings.WIDTH * 0.7F - ((float)texture.getRegionWidth() / 2), 0.0f, (float)texture.getRegionWidth() / 2, 0.0f, texture.getRegionWidth(), texture.getRegionHeight(), Settings.scale, Settings.scale, 0.0f); }
}
