package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.RuinaMod;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_FURIOSO;
import ruina.powers.act5.PlayerBlackSilence;
import ruina.util.TexLoader;

import static ruina.powers.act5.PlayerBlackSilence.THRESHOLD;
import static ruina.util.Wiz.*;

public class BlackSilenceRenderMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID(BlackSilenceRenderMod.class.getSimpleName());

    public static final String STRING = RuinaMod.makeUIPath("silenceImg.png");
    private static final Texture TEXTURE = TexLoader.getTexture(STRING);
    private static final TextureRegion TEXTURE_REGION = new TextureRegion(TEXTURE);
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
        if (active && card != null) {
            sb.draw(TEXTURE_REGION, card.hb.cX - (float) TEXTURE_REGION.getRegionWidth() / 2, card.hb.cY + (card.hb.height / 2) - (float) TEXTURE_REGION.getRegionHeight() / 2, (float) TEXTURE_REGION.getRegionWidth() / 2, (float) TEXTURE_REGION.getRegionHeight() / 2, TEXTURE_REGION.getRegionWidth(), TEXTURE_REGION.getRegionHeight(), Settings.scale * card.drawScale, Settings.scale * card.drawScale, 0.0f);
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
                        active = true;
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
}
