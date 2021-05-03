package ruina.ui;

import basemod.ClickableUIElement;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.actions.common.SelectCardsAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.BobEffect;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import ruina.chr.chr_aya;
import ruina.patches.chr_angela.ExhaustPileViewScreenPatch;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class BookButton extends ClickableUIElement {
    private static final float X_OFF = 0f; // 200f * Settings.scale;
    private static final float Y_OFF = 228f;
    private static final float HB_WIDTH = 128f;
    private static final float HB_HEIGHT = 128f;
    private static final float COUNT_X = 48.0F * Settings.scale;
    private static final float COUNT_Y = -16.0F * Settings.scale;
    private static final float COUNT_OFFSET_X = 48.0F * Settings.scale;
    private static final float COUNT_OFFSET_Y = -18.0F * Settings.scale;
    private static final float DECK_TIP_X = 0F * Settings.scale;
    private static final float DECK_TIP_Y = 128.0F * Settings.scale;
    private final Texture bookButton = TexLoader.getTexture(makeUIPath("bookButton.png"));
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("BookButton"));
    public static final String[] TEXT = uiStrings.TEXT;
    private final BobEffect bob;
    private boolean isOpen = false;

    public BookButton() {
        super((Texture) null,
                0f,
                Y_OFF,
                HB_WIDTH,
                HB_HEIGHT);
        bob = new BobEffect(1.1f);
    }

    @Override
    protected void onHover() {
    }

    @Override
    protected void onUnhover() {
    }

    @Override
    protected void onClick() {
        if (isOpen && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.EXHAUST_VIEW) {
            isOpen = false;
            CardCrawlGame.sound.play("DECK_CLOSE");
            AbstractDungeon.closeCurrentScreen();
        } else if (!AbstractDungeon.isScreenUp) {
            if (bookPile.isEmpty()) {
                AbstractPlayer p = AbstractDungeon.player;
                AbstractDungeon.effectList.add(new ThoughtBubble(p.dialogX, p.dialogY, 3.0F, TEXT[2], true));
            } else {
                ExhaustPileViewScreenPatch.showFrozen = true;
                AbstractDungeon.exhaustPileViewScreen.open();
                isOpen = true;
            }
        }
    }

    @Override
    public void setX(float x) {
        super.setX(x - X_OFF);
    }

    @Override
    public void update() {
        super.update();
        bob.update();
        if (hitbox.hovered && Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) onClick();
        if(hitbox.hovered && InputHelper.justClickedRight){
            atb(new SelectCardsAction(adp().hand.group, adp().hand.size(), "hi", true, c -> true, (cards) -> {
                ArrayList<AbstractCard> bruh = new ArrayList<>(cards);
                for(AbstractCard c: bruh){
                    att(new AbstractGameAction() {
                        @Override
                        public void update() {
                            adp().hand.removeCard(c);
                            bookPile.addToBottom(c.makeStatEquivalentCopy());
                            isDone = true;
                        }
                    });
                }
            }));
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (bookPile != null && (AbstractDungeon.player.chosenClass == chr_aya.Enums.ANGELA_LOR || bookPile.size() > 0)) {
            if (!AbstractDungeon.overlayMenu.combatDeckPanel.isHidden) {
                float x = hitbox.x + hitbox.width / 2f;
                float y = hitbox.y + hitbox.height / 2f;
                sb.setColor(Color.WHITE);
                TexLoader.draw(sb, bookButton, x, y + bob.y);

                String msg = Integer.toString(bookPile.size());
                sb.setColor(Color.WHITE);
                TexLoader.draw(sb,
                        ImageMaster.DECK_COUNT_CIRCLE,
                        x + COUNT_OFFSET_X,
                        y + COUNT_OFFSET_Y);
                FontHelper.renderFontCentered(sb, FontHelper.turnNumFont, msg, x + COUNT_X, y + COUNT_Y);

                hitbox.render(sb);
                if (hitbox.hovered && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.isScreenUp) { TipHelper.renderGenericTip(x + DECK_TIP_X, y + DECK_TIP_Y, TEXT[0], TEXT[1]); }
            }
        }
    }

}