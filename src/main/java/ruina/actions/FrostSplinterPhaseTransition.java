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
    private boolean firstFrame = true;

    private float startingDuration = 2.65f;

    public FrostSplinterPhaseTransition(Act4Angela owner) {
        this.owner = owner;
        duration = startingDuration;
    }

    @Override
    public void update() {
        if(firstFrame){
            owner.runAnim("Special");
            owner.playSound("SnowBlizzard");
            AbstractDungeon.effectsQueue.add(new IceQueenIceEffect());
            firstFrame = false;
        }
        this.tickDuration();
    }
}

