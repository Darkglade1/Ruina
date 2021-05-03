package ruina.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;

import java.util.HashMap;

import static ruina.RuinaMod.makeImagePath;

public class TexLoader {
    private static HashMap<String, Texture> textures = new HashMap<>();

    /**
     * @param textureString - String path to the texture you want to load relative to resources,
     *                      Example: makeImagePath("missing.png")
     * @return <b>com.badlogic.gdx.graphics.Texture</b> - The texture from the path provided
     */
    public static Texture getTexture(final String textureString) {
        if (textures.get(textureString) == null) {
            try {
                loadTexture(textureString, true);
            } catch (GdxRuntimeException e) {
                return getTexture(makeImagePath("ui/missing.png"));
            }
        }
        return textures.get(textureString);
    }

    public static String getCardTextureString(final String cardName, final AbstractCard.CardType cardType) {
        String textureString = makeImagePath("cards/" + cardName + ".png");

        FileHandle h = Gdx.files.internal(textureString);
        if (!h.exists()) {
            switch (cardType) {
                case ATTACK:
                    textureString = makeImagePath("cards/Attack.png");
                    break;
                case SKILL:
                    textureString = makeImagePath("cards/Skill.png");
                    break;
                case POWER:
                    textureString = makeImagePath("cards/Power.png");
                    break;
                default:
                    textureString = makeImagePath("ui/missing.png");
                    break;
            }
        }

        return textureString;
    }

    private static void loadTexture(final String textureString) throws GdxRuntimeException {
        loadTexture(textureString, false);
    }

    private static void loadTexture(final String textureString, boolean linearFilter) throws GdxRuntimeException {
        Texture texture = new Texture(textureString);
        if (linearFilter) {
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        } else {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        textures.put(textureString, texture);
    }

    public static boolean testTexture(String filePath) {
        return Gdx.files.internal(filePath).exists();
    }

    public static TextureAtlas.AtlasRegion getTextureAsAtlasRegion(String textureString) {
        Texture texture = getTexture(textureString);
        return new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
    }


    @SpirePatch(clz = Texture.class, method="dispose")
    public static class DisposeListener {
        @SpirePrefixPatch
        public static void DisposeListenerPatch(final Texture __instance) {
            textures.entrySet().removeIf(entry -> {
                if (entry.getValue().equals(__instance)) System.out.println("TextureLoader | Removing Texture: " + entry.getKey());
                return entry.getValue().equals(__instance);
            });
        }
    }

    public static void draw(SpriteBatch sb, Texture texture, float cX, float cY) { drawScaledAndRotated(sb, texture, cX, cY, 1f, 0f); }

    public static void drawScaledAndRotated(SpriteBatch sb, Texture texture, float cX, float cY, float scale, float rotation) {
        float w = texture.getWidth();
        float h = texture.getHeight();
        float halfW = w / 2f;
        float halfH = h / 2f;
        sb.draw(texture,
                cX - halfW,
                cY - halfH,
                halfW,
                halfH,
                w,
                h,
                scale * Settings.scale,
                scale * Settings.scale,
                rotation,
                0,
                0,
                (int) w,
                (int) h,
                false,
                false);
    }

}