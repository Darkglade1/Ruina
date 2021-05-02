package ruina.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.makeMonsterPath;

public class SerumWAnimation extends AbstractGameEffect {
    AbstractCreature owner;
    AbstractCreature enemy;
    private final float offset = 50.0f * Settings.scale;
    private float graphicsAnimation;
    private float enemyInitialX;
    private float ownerInitialX;
    private float startX;
    private float endX;
    private boolean grabbed = false;
    public static ArrayList<TextureRegion> bgTextures = new ArrayList<>();
    int bgCounter = 0;

    public SerumWAnimation(AbstractCreature enemy, AbstractCreature owner, boolean flipped) {
        this.duration = Settings.ACTION_DUR_FAST;
        this.enemy = enemy;
        this.owner = owner;
        enemyInitialX = enemy.drawX;
        ownerInitialX = owner.drawX;
        if (flipped) {
            endX = Settings.WIDTH;
        } else {
            endX = 0.0f;
        }
        startX = owner.drawX;
        ArrayList<Texture> bgs = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            bgs.add(TexLoader.getTexture(makeMonsterPath("Baral/Backgrounds/BG" + i + ".png")));
        }
        for (Texture texture : bgs) {
            bgTextures.add(new TextureRegion(texture));
        }
    }

    @Override
    public void update() {
        this.graphicsAnimation += Gdx.graphics.getDeltaTime();
        float destinationX = Interpolation.linear.apply(startX, endX, this.graphicsAnimation * 3.0F);
        if (startX > endX) {
            if (destinationX > endX) {
                owner.drawX = destinationX;
                if (grabbed) {
                    enemy.drawX = destinationX - offset;
                }
            } else {
                owner.drawX = endX;
                if (grabbed) {
                    enemy.drawX = endX - offset;
                }
            }
        } else if (startX < endX) {
            if (destinationX < endX) {
                owner.drawX = destinationX;
                if (grabbed) {
                    enemy.drawX = destinationX + offset;
                }
            } else {
                owner.drawX = endX;
                if (grabbed) {
                    enemy.drawX = endX + offset;
                }
            }
        }
        if (Math.abs(owner.drawX - enemy.drawX) <= offset) {
            grabbed = true;
        }
        if (owner.drawX == endX) {
            if (bgCounter < bgTextures.size() - 1) {
                bgCounter++;
                startX = Math.abs(endX - Settings.WIDTH);
                graphicsAnimation = 0.0f;
                AbstractRuinaMonster.playSound("ClawUltiMove");
            } else {
                owner.drawX = ownerInitialX;
                enemy.drawX = enemyInitialX;
                AbstractRuinaMonster.playSound("ClawUltiEnd");
                this.isDone = true;
            }
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        TextureRegion region = bgTextures.get(bgCounter);
        sb.draw(region, 0.0F, 0.0F, 0.0f, 0.0f, region.getRegionWidth(), region.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
        enemy.render(sb);
        owner.render(sb);
    }

    @Override
    public void dispose() {

    }
}


