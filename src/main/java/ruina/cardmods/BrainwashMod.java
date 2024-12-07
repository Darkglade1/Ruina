package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import ruina.RuinaMod;
import ruina.util.TexLoader;

public class BrainwashMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID(BrainwashMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    public static final String STRING = RuinaMod.makeMonsterPath("Oswald/Brainwashed.png");
    private static final Texture TEXTURE = TexLoader.getTexture(STRING);
    private static final TextureRegion TEXTURE_REGION = new TextureRegion(TEXTURE);
    private boolean alreadyShuffleBack = false;

    @Override
    public AbstractCardModifier makeCopy() {
        return new BrainwashMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.shuffleBackIntoDrawPile) {
            card.shuffleBackIntoDrawPile = true;
        } else {
            alreadyShuffleBack = true;
        }
        if (card.cost > 0) {
            card.cost = 0;
            card.costForTurn = 0;
            card.isCostModified = true;
        }
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!alreadyShuffleBack) {
            card.shuffleBackIntoDrawPile = false;
        }
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (!rawDescription.contains(TEXT[0])) {
            return TEXT[0] + rawDescription;
        } else {
            return rawDescription;
        }
    }

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        sb.draw(TEXTURE_REGION, card.hb.cX - (float) TEXTURE_REGION.getRegionWidth() / 2, card.hb.cY + (card.hb.height / 2) - (float) TEXTURE_REGION.getRegionHeight() / 2, (float) TEXTURE_REGION.getRegionWidth() / 2, (float) TEXTURE_REGION.getRegionHeight() / 2, TEXTURE_REGION.getRegionWidth(), TEXTURE_REGION.getRegionHeight(), Settings.scale * card.drawScale, Settings.scale * card.drawScale, 0.0f);
    }
}
