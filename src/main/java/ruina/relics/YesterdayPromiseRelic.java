package ruina.relics;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.cardmods.ContractsMod;

import static ruina.RuinaMod.makeID;

public class YesterdayPromiseRelic extends AbstractEasyRelic {
    public static final String ID = makeID(YesterdayPromiseRelic.class.getSimpleName());

    public YesterdayPromiseRelic() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onEquip() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            CardModifierManager.addModifier(c, new ContractsMod());
        }
    }

    @Override
    public void onPreviewObtainCard(AbstractCard c) {
        this.onObtainCard(c);
    }

    @Override
    public void onObtainCard(AbstractCard c) {
        CardModifierManager.addModifier(c, new ContractsMod());
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}