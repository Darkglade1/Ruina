package ruina.shaders.Aspiration;

import basemod.interfaces.ScreenPostProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ShaderHelper;

import static ruina.RuinaMod.makeShaderPath;

public class AspirationPostProcessor implements ScreenPostProcessor {

    private static final String VERTEX_PATH = makeShaderPath("Aspiration/vertex.vs");
    private static final String FRAGMENT_PATH = makeShaderPath("Aspiration/fragment.fs");
    private float u_time = 0f;

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
        shaderEffect.setUniform2fv("u_resolution", new float[] {Settings.WIDTH, Settings.HEIGHT }, 0, 2);
        sb.setColor(Color.WHITE.cpy());
        sb.draw(textureRegion, 0f, 0f);
        ShaderHelper.setShader(sb, ShaderHelper.Shader.DEFAULT);

    }
}
