package ruina.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.RuinaMod;
import ruina.relics.SeventhBullet;
import ruina.util.TexLoader;

@SpirePatch(clz = AbstractCard.class, method = "renderEnergy")
public class SeventhBulletReminderPatch {

    public static final String STRING = RuinaMod.makePowerPath("SeventhBullet84.png");
    private static final Texture reminderTexture = TexLoader.getTexture(STRING);
    private static final TextureAtlas.AtlasRegion reminderRegion = new TextureAtlas.AtlasRegion(reminderTexture, 0, 0, reminderTexture.getWidth(), reminderTexture.getHeight());

    @SpirePostfixPatch
    public static void patch(AbstractCard __instance, SpriteBatch sb) {
        if (AbstractDungeon.player.hasRelic(SeventhBullet.ID) && AbstractDungeon.player.getRelic(SeventhBullet.ID).counter == SeventhBullet.CARDS_THRESHOLD - 1 && __instance.type == AbstractCard.CardType.ATTACK) {
            sb.setColor(Color.WHITE);
            renderHelper(sb, reminderRegion, __instance.current_x, __instance.current_y, __instance);
            sb.setBlendFunction(770, 1);
            sb.setColor(new Color(1.0F, 1.0F, 1.0F, (MathUtils.cosDeg((float) (System.currentTimeMillis() / 5L % 360L)) + 1.25F) / 3.0F));
            renderHelper(sb, reminderRegion, __instance.current_x, __instance.current_y, __instance);
            sb.setBlendFunction(770, 771);
            sb.setColor(Color.WHITE);
        }
    }

    private static void renderHelper(SpriteBatch sb, TextureAtlas.AtlasRegion img, float drawX, float drawY, AbstractCard C) {
        sb.draw(img, drawX + (100.0f * Settings.scale) + img.offsetX - (float) img.originalWidth / 2.0F, drawY + (C.hb.height / 2) + img.offsetY - (float) img.originalHeight / 2.0F, (float) img.originalWidth / 2.0F - img.offsetX, (float) img.originalHeight / 2.0F - img.offsetY, (float) img.packedWidth, (float) img.packedHeight, C.drawScale * Settings.scale, C.drawScale * Settings.scale, C.angle);
    }

}