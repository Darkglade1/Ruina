package ruina.monsters.theHead.dialogue.speaker;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

}