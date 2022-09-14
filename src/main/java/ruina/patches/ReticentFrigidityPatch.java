package ruina.patches;

import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import ruina.RuinaMod;
import ruina.cardmods.FrozenMod;
import ruina.cardmods.ReticentFrigidityMod;
import ruina.powers.ColdHearted;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class ReticentFrigidityPatch {

    private static TextureAtlas.AtlasRegion uiIcon = RuinaMod.UIAtlasDuplicate.findRegion("freezeImg");

    private static boolean shouldShowButtons(AbstractCard __instance) {
        return (CardModifierManager.hasModifier(__instance, ReticentFrigidityMod.ID) && AbstractDungeon.player != null && !AbstractDungeon.player.exhaustPile.contains(__instance));
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class HoveredButtonField {
        public static SpireField<Boolean> buttonHovered = new SpireField<>(() -> false);
    }

    @SpirePatch(clz = AbstractCard.class, method = "update")
    public static class detectHover {
        public static boolean FORCE_REG_CURSOR = false;

        public static float START_X = 210;
        public static float END_X = START_X + 70;
        public static float START_Y = 360;
        public static float END_Y = START_Y + 40;

        public static void Postfix(AbstractCard __instance) {
            if (shouldShowButtons(__instance)) {
                HoveredButtonField.buttonHovered.set(__instance, false);
                if (InputHelper.mX >= __instance.hb.x + (START_X * Settings.scale) && InputHelper.mX <= __instance.hb.x + (END_X * Settings.scale) && InputHelper.mY >= __instance.hb.y + (START_Y * Settings.scale) && InputHelper.mY <= __instance.hb.y + (END_Y * Settings.scale)) {
                    HoveredButtonField.buttonHovered.set(__instance, true);
                    FORCE_REG_CURSOR = true;
                    if (InputHelper.justClickedRight) {
                        CardModifierManager.removeModifiersById(__instance, ReticentFrigidityMod.ID, false);
                        CardModifierManager.addModifier(__instance, new FrozenMod());
                        atb(new ApplyPowerAction(adp(), adp(), new ColdHearted(adp(), 1)));
                    }
                }
            }
        }
    }


    @SpirePatch(clz = AbstractCard.class, method = "renderEnergy")
    public static class SecondEnergyRenderPatch {
        public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
            if (shouldShowButtons(__instance)) {
                Color g = Color.WHITE.cpy();
                if (HoveredButtonField.buttonHovered.get(__instance)) {
                    g = Color.LIGHT_GRAY.cpy();
                }
                g.a = __instance.transparency;
                renderHelper(sb, g, uiIcon, __instance.current_x, __instance.current_y, __instance);
            }
        }
    }

    private static void renderHelper(SpriteBatch sb, Color color, TextureAtlas.AtlasRegion img, float drawX, float drawY, AbstractCard q) {
        sb.setColor(color);
        sb.draw(img, drawX + img.offsetX - (float) img.originalWidth / 2.0F, drawY + img.offsetY - (float) img.originalHeight / 2.0F, (float) img.originalWidth / 2.0F - img.offsetX, (float) img.originalHeight / 2.0F - img.offsetY, (float) img.packedWidth, (float) img.packedHeight, q.drawScale * Settings.scale, q.drawScale * Settings.scale, q.angle);
    }
}
