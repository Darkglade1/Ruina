package ruina.util;

import basemod.BaseMod;
import basemod.ClickableUIElement;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import ruina.monsters.AbstractAllyMonster;

import static ruina.util.Wiz.adp;

public class AllyMove extends ClickableUIElement {

    private final String ID;
    private final String moveDescription;
    private final Runnable moveActions;
    private Runnable moveActions2;
    private final AbstractAllyMonster owner;

    public AllyMove(String ID, AbstractAllyMonster owner, Texture moveImage, String moveDescription, Runnable moveActions) {
        super(moveImage, 0, 0, 64.0f, 64.0f);
        this.moveActions = moveActions;
        this.ID = ID;
        this.moveDescription = moveDescription;
        this.owner = owner;
    }

    public AllyMove(String ID, AbstractAllyMonster owner, Texture moveImage, String moveDescription, Runnable moveActions, Runnable moveActions2) {
        this(ID, owner, moveImage, moveDescription, moveActions);
        this.moveActions2 = moveActions2;
    }

    private void doMove() {
        if(moveActions != null) {
            moveActions.run();
        } else {
            BaseMod.logger.info("AllyMove: " + this.ID + " had no actions!");
        }
    }

    private void doMove2() {
        if(moveActions2 != null) {
            moveActions2.run();
        } else {
            BaseMod.logger.info("AllyMove: " + this.ID + " had no secondary actions!");
        }
    }

    public String getID(){
        return this.ID;
    }

    @Override
    protected void onHover() {
        TipHelper.renderGenericTip(this.x, this.y - 15f * Settings.scale, this.ID, this.moveDescription);
        if (this.hitbox.justHovered && canUse()) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
        }
    }

    @Override
    protected void onUnhover() {

    }

    private boolean canUse() {
        return !AbstractDungeon.actionManager.turnHasEnded && !adp().inSingleTargetMode && !adp().isDraggingCard && !AbstractDungeon.isScreenUp;
    }

    @Override
    protected void onClick() {
        if(canUse()){
            InputHelper.justClickedLeft = false;
            CInputActionSet.select.unpress();
            CardCrawlGame.sound.play("UI_CLICK_1");
            this.doMove();
        }
    }

    protected void onClick2() {
        if(canUse()){
            InputHelper.justClickedRight = false;
            CInputActionSet.select.unpress();
            CardCrawlGame.sound.play("UI_CLICK_1");
            this.doMove2();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if(!canUse()){
            super.render(sb, Color.GRAY);
        } else if (this.hitbox.hovered) {
            super.render(sb, Color.GOLD);
        } else {
            super.render(sb);
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.hitbox.hovered && InputHelper.justClickedRight && isClickable()) {
            this.onClick2();
        }

    }
}