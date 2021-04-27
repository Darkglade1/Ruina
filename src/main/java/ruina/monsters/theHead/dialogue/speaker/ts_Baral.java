package ruina.monsters.theHead.dialogue.speaker;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import ruina.RuinaMod;
import ruina.monsters.theHead.dialogue.AbstractSpeaker;
import ruina.util.TexLoader;

public class ts_Baral extends AbstractSpeaker {

    public ts_Baral(){
        idleSprite = TexLoader.getTexture(RuinaMod.makeUIPath("Baral.png"));
        speakingSprite = TexLoader.getTexture(RuinaMod.makeUIPath("Baral_nospeak.png"));
        internalSpeakerName = "Baral";
        internalSpeakerKey = internalSpeakerName + "Speak";
    }

    public void drawSprite(SpriteBatch sb, TextureRegion texture, int charsOnScreen) { sb.draw(texture, Settings.WIDTH  * 0.85f - ((float)texture.getRegionWidth() / 2), 0.0f, (float)texture.getRegionWidth() / 2, 0.0f, texture.getRegionWidth(), texture.getRegionHeight(), Settings.scale, Settings.scale, 0.0f); }


}