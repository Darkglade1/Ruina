package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.monsters.AbstractRuinaMonster;

public class Mimicry extends AbstractGameEffect {
    public Mimicry() {
        this.color = Color.RED.cpy();
        this.color.a = 0.75F;
        this.duration = 2.0F;
        this.renderBehind = true;
    }

    public void update() {
        if (this.duration == 2.0F) {
            for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                m.tint.color.set(Color.BLACK.cpy());
                m.tint.changeColor(Color.BLACK.cpy());
            }
            AbstractDungeon.player.tint.color.set(Color.BLACK.cpy());
            AbstractDungeon.player.tint.changeColor(Color.BLACK.cpy());
            AbstractRuinaMonster.playSound("Goodbye");
        }
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                m.tint.color.set(Color.WHITE.cpy());
                m.tint.changeColor(Color.WHITE.cpy());
            }
            AbstractDungeon.player.tint.color.set(Color.WHITE.cpy());
            AbstractDungeon.player.tint.changeColor(Color.WHITE.cpy());
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.BLACK.cpy());
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
        sb.setColor(this.color);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
    }

    public void dispose() {}
}