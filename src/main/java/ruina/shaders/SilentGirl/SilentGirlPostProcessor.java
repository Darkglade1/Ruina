package ruina.shaders.SilentGirl;

import basemod.interfaces.ScreenPostProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ShaderHelper;

import static ruina.RuinaMod.makeShaderPath;

public class SilentGirlPostProcessor implements ScreenPostProcessor {

    private static final String VERTEX_PATH = makeShaderPath("SilentGirl/vertex.vs");
    private static final String FRAGMENT_PATH = makeShaderPath("SilentGirl/fragment.fs");
    private float u_time = 0f;
    private int u_variation = MathUtils.random(2500);

    private static final ShaderProgram shaderEffect = new ShaderProgram(
            Gdx.files.internal(VERTEX_PATH).readString(),
            Gdx.files.internal(FRAGMENT_PATH).readString()
    );

    @Override
    public void postProcess(SpriteBatch sb, TextureRegion textureRegion, OrthographicCamera camera) {
        sb.end();
        sb.setShader(shaderEffect);
        sb.begin();
        u_time += Gdx.graphics.getDeltaTime();
        shaderEffect.setUniform1fv("u_time", new float[] { u_time }, 0, 1);
        shaderEffect.setUniformi("u_variation", u_variation);
        shaderEffect.setUniform2fv("u_resolution", new float[] {Settings.WIDTH, Settings.HEIGHT }, 0, 2);
        sb.setColor(Color.WHITE.cpy());
        sb.draw(textureRegion, 0f, 0f);
        ShaderHelper.setShader(sb, ShaderHelper.Shader.DEFAULT);

    }

}
