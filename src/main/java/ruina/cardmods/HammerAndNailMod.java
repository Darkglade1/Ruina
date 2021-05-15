package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import ruina.RuinaMod;
import ruina.util.TexLoader;

public class HammerAndNailMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID("HammerAndNailMod");

    public static final String HAMMER = RuinaMod.makeMonsterPath("SilentGirl/HammerCardIcon.png");
    private static final Texture HAMMER_TEXTURE = TexLoader.getTexture(HAMMER);
    private static final TextureRegion HAMMER_TEXTURE_REGION = new TextureRegion(HAMMER_TEXTURE);

    public static final String NAIL = RuinaMod.makeMonsterPath("SilentGirl/NailCardIcon.png");
    private static final Texture NAIL_TEXTURE = TexLoader.getTexture(NAIL);
    private static final TextureRegion NAIL_TEXTURE_REGION = new TextureRegion(NAIL_TEXTURE);

    @Override
    public AbstractCardModifier makeCopy() {
        return new HammerAndNailMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        if (card.costForTurn >= 0) {
            TextureRegion TEXTURE_REGION = null;
            if (GameActionManager.turn % 2 == 1 && card.costForTurn % 2 == 1) {
                TEXTURE_REGION = NAIL_TEXTURE_REGION;
            } else if (GameActionManager.turn % 2 == 0 && card.costForTurn % 2 == 0) {
                TEXTURE_REGION = HAMMER_TEXTURE_REGION;
            }
            if (TEXTURE_REGION != null) {
                sb.draw(TEXTURE_REGION, card.hb.cX - (float) TEXTURE_REGION.getRegionWidth() / 2, card.hb.y + card.hb.height - (float)TEXTURE_REGION.getRegionHeight() / 2, (float) TEXTURE_REGION.getRegionWidth() / 2, (float) TEXTURE_REGION.getRegionHeight() / 2, TEXTURE_REGION.getRegionWidth(), TEXTURE_REGION.getRegionHeight(), Settings.scale * card.drawScale, Settings.scale * card.drawScale, 0.0f);
            }
        }
    }
}
