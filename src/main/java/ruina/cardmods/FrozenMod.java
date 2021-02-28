package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.CommonKeywordIconsField;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import ruina.RuinaMod;
import ruina.util.HelperClass;
import ruina.util.TexLoader;

public class FrozenMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID("FrozenMod");

    public static final String FROZEN = RuinaMod.makeMonsterPath("SnowQueen/FrozenCard.png");
    private static final Texture FROZEN_TEXTURE = TexLoader.getTexture(FROZEN);
    private static final TextureRegion FROZEN_TEXTURE_REGION = new TextureRegion(FROZEN_TEXTURE);

    private boolean alreadyRetain = false;

    @Override
    public AbstractCardModifier makeCopy() {
        return new FrozenMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.selfRetain) {
            card.selfRetain = true;
            alreadyRetain = false;
        } else {
            alreadyRetain = true;
        }
    }

    @Override
    public void onRemove(AbstractCard card) {
        if (!alreadyRetain) {
            card.selfRetain = false;
        }
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        return false;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        String description = HelperClass.capitalize(GameDictionary.UNPLAYABLE.NAMES[0]) + LocalizedStrings.PERIOD + " NL ";
        if (!alreadyRetain && !(CommonKeywordIconsField.useIcons.get(card))) {
            description += HelperClass.capitalize(GameDictionary.RETAIN.NAMES[0]) + LocalizedStrings.PERIOD + " NL ";
        }
        description += rawDescription;
        return description;
    }

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        sb.draw(FROZEN_TEXTURE_REGION, card.hb.cX - (float)FROZEN_TEXTURE_REGION.getRegionWidth() / 2 * Settings.scale, card.hb.cY - (float)FROZEN_TEXTURE_REGION.getRegionHeight() / 2 * Settings.scale, (float)FROZEN_TEXTURE_REGION.getRegionWidth() / 2, (float)FROZEN_TEXTURE_REGION.getRegionHeight() / 2, FROZEN_TEXTURE_REGION.getRegionWidth(), FROZEN_TEXTURE_REGION.getRegionHeight(), Settings.scale * card.drawScale, Settings.scale * card.drawScale, 0.0f);
    }
}
