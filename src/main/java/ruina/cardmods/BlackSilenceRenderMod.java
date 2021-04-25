package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.CommonKeywordIconsField;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.RuinaMod;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_FURIOSO;
import ruina.powers.PlayerBlackSilence;
import ruina.util.HelperClass;

import static ruina.powers.PlayerBlackSilence.THRESHOLD;
import static ruina.util.Wiz.*;

public class BlackSilenceRenderMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID(BlackSilenceRenderMod.class.getSimpleName());

    private static TextureAtlas.AtlasRegion uiIcon = RuinaMod.UIAtlas.findRegion("silenceImg");
    private boolean active = true;

    @Override
    public AbstractCardModifier makeCopy() {
        return new BlackSilenceRenderMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        if(active) {
            Color cardColor = Color.WHITE.cpy();
            cardColor.a = card.transparency;
            renderHelper(sb, cardColor, uiIcon, card.current_x, card.current_y, card);
        }
    }

    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractPower p = adp().getPower(PlayerBlackSilence.POWER_ID);
                if(p != null && active){
                    p.amount++;
                    active = false;
                    if(p.amount >= THRESHOLD){
                        p.flash();
                        p.amount = 0;
                        AbstractCard furioso = new CHRALLY_FURIOSO(((PlayerBlackSilence) p).parent);
                        CardModifierManager.addModifier(furioso, new RetainMod());
                        CardModifierManager.addModifier(furioso, new ExhaustMod());
                        att(new MakeTempCardInHandAction(furioso, 1));
                        for(AbstractCard c: adp().hand.group){
                            if(CardModifierManager.hasModifier(c, BlackSilenceRenderMod.ID)) {
                                for (AbstractCardModifier r : CardModifierManager.getModifiers(c, BlackSilenceRenderMod.ID)) {
                                    if (r instanceof BlackSilenceRenderMod) { ((BlackSilenceRenderMod) r).active = true; }
                                }
                            }
                        }
                        for(AbstractCard c: adp().drawPile.group){
                            if(CardModifierManager.hasModifier(c, BlackSilenceRenderMod.ID)) {
                                for (AbstractCardModifier r : CardModifierManager.getModifiers(c, BlackSilenceRenderMod.ID)) {
                                    if (r instanceof BlackSilenceRenderMod) { ((BlackSilenceRenderMod) r).active = true; }
                                }
                            }
                        }
                        for(AbstractCard c: adp().discardPile.group){
                            if(CardModifierManager.hasModifier(c, BlackSilenceRenderMod.ID)) {
                                for (AbstractCardModifier r : CardModifierManager.getModifiers(c, BlackSilenceRenderMod.ID)) {
                                    if (r instanceof BlackSilenceRenderMod) { ((BlackSilenceRenderMod) r).active = true; }
                                }
                            }
                        }
                    }
                }
                isDone = true;
            }
        });
    }

    private static void renderHelper(SpriteBatch sb, Color color, TextureAtlas.AtlasRegion img, float drawX, float drawY, AbstractCard q) {
        sb.setColor(color);
        sb.draw(img, drawX + img.offsetX - (float) img.originalWidth / 2.0F, drawY + img.offsetY - (float) img.originalHeight / 2.0F, (float) img.originalWidth / 2.0F - img.offsetX, (float) img.originalHeight / 2.0F - img.offsetY, (float) img.packedWidth, (float) img.packedHeight, q.drawScale * Settings.scale, q.drawScale * Settings.scale, q.angle);
    }
}
