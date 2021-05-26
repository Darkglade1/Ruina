package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import ruina.RuinaMod;
import ruina.util.TexLoader;

public class LaurelWreathMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID("LaurelWreath");

    public static final String STRING = RuinaMod.makeMonsterPath("Alriune/LaurelWreath.png");
    private static final Texture TEXTURE = TexLoader.getTexture(STRING);
    private static final TextureRegion TEXTURE_REGION = new TextureRegion(TEXTURE);

    @Override
    public AbstractCardModifier makeCopy() {
        return new LaurelWreathMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        sb.draw(TEXTURE_REGION, card.hb.cX - (float) TEXTURE_REGION.getRegionWidth() / 2, card.hb.cY + (card.hb.height / 2) - (float) TEXTURE_REGION.getRegionHeight() / 2, (float) TEXTURE_REGION.getRegionWidth() / 2, (float) TEXTURE_REGION.getRegionHeight() / 2, TEXTURE_REGION.getRegionWidth(), TEXTURE_REGION.getRegionHeight(), Settings.scale * card.drawScale, Settings.scale * card.drawScale, 0.0f);
    }
}
