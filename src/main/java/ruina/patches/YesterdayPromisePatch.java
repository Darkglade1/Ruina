package ruina.patches;


import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.cardmods.ContractsMod;
import ruina.cardmods.UnplayableMod;
import ruina.relics.YesterdayPromiseRelic;

@SpirePatch(
        clz= AbstractCard.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez= {
                String.class,
                String.class,
                String.class,
                int.class,
                String.class,
                AbstractCard.CardType.class,
                AbstractCard.CardColor.class,
                AbstractCard.CardRarity.class,
                AbstractCard.CardTarget.class,
                DamageInfo.DamageType.class
        }
)
public class YesterdayPromisePatch
{
    @SpirePostfixPatch
    public static void contractPatch(AbstractCard __instance, String id, String name, String imgUrl, int cost, String rawDescription, AbstractCard.CardType type, AbstractCard.CardColor color, AbstractCard.CardRarity rarity, AbstractCard.CardTarget target, DamageInfo.DamageType dType) {
        if(AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(YesterdayPromiseRelic.ID)){ CardModifierManager.addModifier(__instance, new ContractsMod()); }
    }
}