package ruina.vfx.TerminationProtocol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import static ruina.RuinaMod.makeUIPath;

public class TerminationRabbitImage extends AbstractGameEffect {

    private Texture rabbitIcon;

    public TerminationRabbitImage(){
        this.duration = 1.8F;
        this.startingDuration = this.duration;
        this.color = new Color(1, 0.21F, 0, 0.76F);
        this.scale = 0.01F;
        rabbitIcon = new Texture(makeUIPath("RabbitTeam.png"));
    }

    public void update(){
        this.duration -= Gdx.graphics.getDeltaTime();
        if(this.duration <= 0){
            this.isDone = true;
        }

        if(this.duration > 1.2F){
            this.scale = (this.startingDuration - this.duration) / 0.6F;
        }
        else {
            this.scale = 1;
        }

        if(this.duration < 0.4F){
            this.color.a = this.duration * 1.9F;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, Settings.HEIGHT * 0.39F, Settings.WIDTH, Settings.HEIGHT * 0.22F);
        Color tmp = Color.WHITE.cpy();
        tmp.a = this.color.a / 0.8F;
        sb.setColor(tmp);
        float height = Settings.HEIGHT * 0.28F * this.scale, width = height / rabbitIcon.getHeight() * rabbitIcon.getWidth();
        sb.draw(rabbitIcon, (Settings.WIDTH - width) / 2, (Settings.HEIGHT - height) / 2, width, height);
    }

    public void dispose(){}
}