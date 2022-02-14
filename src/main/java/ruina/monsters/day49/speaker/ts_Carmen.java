package ruina.monsters.day49.speaker;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import ruina.RuinaMod;
import ruina.monsters.theHead.dialogue.AbstractSpeaker;
import ruina.util.TexLoader;

public class ts_Carmen extends AbstractSpeaker {

    public ts_Carmen(){
        idleSprite = TexLoader.getTexture(RuinaMod.makeUIPath("Carmen.png"));
        speakingSprite = TexLoader.getTexture(RuinaMod.makeUIPath("Carmen_nospeak.png"));
        internalSpeakerName = "Carmen";
        internalSpeakerKey = internalSpeakerName + "Speak";
    }

    public void drawSprite(SpriteBatch sb, TextureRegion texture, int charsOnScreen) {
        sb.draw(texture, Settings.WIDTH * 0.7F - ((float)texture.getRegionWidth() / 2), 0.0f, (float)texture.getRegionWidth() / 2, 0.0f, texture.getRegionWidth(), texture.getRegionHeight(), Settings.scale, Settings.scale, 0.0f); }
}
