package ruina.monsters.theHead.dialogue.speaker;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

}