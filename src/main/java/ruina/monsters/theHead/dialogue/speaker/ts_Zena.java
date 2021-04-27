package ruina.monsters.theHead.dialogue.speaker;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ruina.RuinaMod;
import ruina.monsters.theHead.Zena;
import ruina.monsters.theHead.dialogue.AbstractSpeaker;
import ruina.util.TexLoader;

public class ts_Zena extends AbstractSpeaker {

    public ts_Zena(){
        idleSprite = TexLoader.getTexture(RuinaMod.makeUIPath("Zena.png"));
        speakingSprite = TexLoader.getTexture(RuinaMod.makeUIPath("Zena_nospeak.png"));
        internalSpeakerName = "Zena";
        internalSpeakerKey = internalSpeakerName + "Speak";
    }

}