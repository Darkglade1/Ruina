package ruina.util;

import basemod.BaseMod;
import basemod.ClickableUIElement;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import ruina.monsters.AbstractAllyMonster;

import static ruina.util.Wiz.adp;


public class AllyMove extends ClickableUIElement {

    private String ID;
    private String moveDescription;
    private Texture moveImage;
    private Runnable moveActions;
    private AbstractAllyMonster owner;

    public AllyMove(String ID, AbstractAllyMonster owner, Texture moveImage, String moveDescription, Runnable moveActions) {
        super(moveImage, 0, 0, 64.0f, 64.0f);
        this.moveImage = moveImage;
        this.moveActions = moveActions;
        this.ID = ID;
        this.moveDescription = moveDescription;
        this.owner = owner;
    }

    private void doMove() {
        if(moveActions != null) {
            moveActions.run();
        } else {
            BaseMod.logger.info("MinionMove: " + this.ID + " had no actions!");
        }
    }

    public Hitbox getHitbox() {
        return this.hitbox;
    }

    public Texture getMoveImage(){
        return this.moveImage;
    }

    public void setMoveImage(Texture moveImage){
        this.moveImage = moveImage;
    }

    public String getID(){
        return this.ID;
    }

    public String getMoveDescription(){
        return this.moveDescription;
    }

    @Override
    protected void onHover() {
        TipHelper.renderGenericTip(this.x, this.y - 15f * Settings.scale, this.ID, this.moveDescription);
    }

    @Override
    protected void onUnhover() {

    }

    @Override
    protected void onClick() {
        if(!AbstractDungeon.actionManager.turnHasEnded && !adp().inSingleTargetMode && !adp().isDraggingCard){
            this.doMove();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if(AbstractDungeon.actionManager.turnHasEnded){
            super.render(sb, Color.GRAY);
        } else {
            super.render(sb);
        }

    }
}