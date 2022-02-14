package ruina.monsters.day49.speaker;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import ruina.RuinaMod;
import ruina.monsters.theHead.dialogue.AbstractSpeaker;
import ruina.util.TexLoader;

public class ts_BloodBath extends AbstractSpeaker {

    public ts_BloodBath(){
        idleSprite = TexLoader.getTexture(RuinaMod.makeUIPath("BloodBath.png"));
        speakingSprite = TexLoader.getTexture(RuinaMod.makeUIPath("BloodBath_nospeak.png"));
        internalSpeakerName = "BloodBath";
        internalSpeakerKey = internalSpeakerName + "Speak";
    }

    public void drawSprite(SpriteBatch sb, TextureRegion texture, int charsOnScreen) { sb.draw(texture, Settings.WIDTH * 0.7F - ((float)texture.getRegionWidth() / 2), 0.0f, (float)texture.getRegionWidth() / 2, 0.0f, texture.getRegionWidth(), texture.getRegionHeight(), Settings.scale, Settings.scale, 0.0f); }
}