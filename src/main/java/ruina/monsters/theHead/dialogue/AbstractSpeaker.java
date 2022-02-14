package ruina.monsters.theHead.dialogue;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;

public abstract class AbstractSpeaker {

    public Texture speakingSprite;
    public Texture idleSprite;
    public TextureRegion activeSprite;
    private boolean speaking;
    public String internalSpeakerKey;
    public String internalSpeakerName;

    private boolean show = false;

    public AbstractSpeaker(){

    }

    public void setTexture(String passedKey){
        speaking = internalSpeakerKey.equals(passedKey);
        activeSprite = speaking ? new TextureRegion(idleSprite) : new TextureRegion(speakingSprite);
    }

    public void render(SpriteBatch sb, int charsOnScreen){
        if(show){ drawSprite(sb, activeSprite, charsOnScreen); }
    }

    public void drawSprite(SpriteBatch sb, TextureRegion texture, int charsOnScreen) { sb.draw(texture, Settings.WIDTH * 0.75F - ((float)texture.getRegionWidth() / 2), 0.0f, (float)texture.getRegionWidth() / 2, 0.0f, texture.getRegionWidth(), texture.getRegionHeight(), Settings.scale, Settings.scale, 0.0f); }

    public void setShowBasedOnCharList(String characters){ show = characters.contains(internalSpeakerName); }

    public boolean getShow(){ return show;}
}
