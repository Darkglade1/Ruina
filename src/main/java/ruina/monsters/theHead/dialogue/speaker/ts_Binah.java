package ruina.monsters.theHead.dialogue.speaker;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import ruina.RuinaMod;
import ruina.monsters.theHead.dialogue.AbstractSpeaker;
import ruina.util.TexLoader;

public class ts_Binah extends AbstractSpeaker {

    public ts_Binah(){
        idleSprite = TexLoader.getTexture(RuinaMod.makeUIPath("Binah.png"));
        speakingSprite = TexLoader.getTexture(RuinaMod.makeUIPath("Binah_nospeak.png"));
        internalSpeakerName = "Binah";
        internalSpeakerKey = internalSpeakerName + "Speak";
    }

    public void drawSprite(SpriteBatch sb, TextureRegion texture, int charsOnScreen) { sb.draw(texture, Settings.WIDTH * 0.75F - ((float)texture.getRegionWidth() / 2), 0.0f, (float)texture.getRegionWidth() / 2, 0.0f, texture.getRegionWidth(), texture.getRegionHeight(), Settings.scale, Settings.scale, 0.0f); }


}