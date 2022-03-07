package ruina.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.day49.Act4Angela;
import ruina.vfx.BurrowingHeavenEffect;
import ruina.vfx.IceQueenIceEffect;

import java.util.ArrayList;
import java.util.Collections;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class FrostSplinterPhaseTransition extends AbstractGameAction {
    Act4Angela owner;
    private float ownerInitialX;
    private boolean firstFrame = true;
    private boolean specialAnimationUsed = false;
    private boolean freezeEffectUsed = false;

    private float startingDuration = 5f;

    public FrostSplinterPhaseTransition(Act4Angela owner) {
        this.duration = Settings.ACTION_DUR_FAST;
        this.owner = owner;
        ownerInitialX = owner.drawX;
        duration = startingDuration;
    }

    @Override
    public void update() {
        if(firstFrame){
            //CardCrawlGame.fadeIn(3.5f);
            adp().hideHealthBar();
            //AbstractDungeon.player.drawX += 500.0F * Settings.scale;
            //AbstractDungeon.player.dialogX += 500.0F * Settings.scale;
            //owner.drawX = adp().drawX;
            ///AbstractDungeon.player.drawX -= 500.0F * Settings.scale;
            //AbstractDungeon.player.dialogX -= 500.0F * Settings.scale;
            //adp().showHealthBar();
            firstFrame = false;
        }
        else if (duration <= startingDuration - 2.5f && !specialAnimationUsed)
        {
            owner.runAnim("Special");
            owner.playSound("SnowBlizzard");
            AbstractDungeon.effectsQueue.add(new IceQueenIceEffect());
            specialAnimationUsed = true;
        }
        else if (duration <= startingDuration - 3.5f && !freezeEffectUsed)
        {
            //AbstractDungeon.effectsQueue.add(new BurrowingHeavenEffect());
            freezeEffectUsed = true;
        }
        this.tickDuration();
    }
}

