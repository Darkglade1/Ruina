package ruina.monsters.theHead.dialogue.speaker;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import ruina.RuinaMod;
import ruina.monsters.theHead.dialogue.AbstractSpeaker;
import ruina.util.TexLoader;

public class ts_Gebura extends AbstractSpeaker {

    public ts_Gebura(){
        idleSprite = TexLoader.getTexture(RuinaMod.makeUIPath("Gebura.png"));
        speakingSprite = TexLoader.getTexture(RuinaMod.makeUIPath("Gebura_nospeak.png"));
        internalSpeakerName = "Gebura";
        internalSpeakerKey = internalSpeakerName + "Speak";
    }

    public void drawSprite(SpriteBatch sb, TextureRegion texture, int charsOnScreen) { sb.draw(texture, Settings.WIDTH * 0.75F - ((float)texture.getRegionWidth() / 2), 0.0f, (float)texture.getRegionWidth() / 2, 0.0f, texture.getRegionWidth(), texture.getRegionHeight(), Settings.scale, Settings.scale, 0.0f); }


}