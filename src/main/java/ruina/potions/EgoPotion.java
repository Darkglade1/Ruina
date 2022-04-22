package ruina.potions;

import basemod.abstracts.CustomPotion;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import ruina.RuinaMod;
import ruina.actions.FlexibleDiscoveryAction;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.util.Wiz.atb;

public class EgoPotion extends CustomPotion {

    public static final String POTION_ID = RuinaMod.makeID("EgoPotion");
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(POTION_ID);

    public static final String NAME = potionStrings.NAME;
    public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;

    public EgoPotion() {
        super(NAME, POTION_ID, PotionRarity.UNCOMMON, PotionSize.MOON, PotionColor.FEAR);
        isThrown = false;
    }

    @Override
    public void use(AbstractCreature target) {
        atb(new FlexibleDiscoveryAction(AbstractEgoCard.getNeowChooseEGOCard(), potency, true));
    }

    @Override
    public int getPotency(final int ascensionLevel) {
        return 1;
    }

    @Override
    public void initializeData() {
        this.potency = this.getPotency();
        if (potency > 1) {
            this.description = potionStrings.DESCRIPTIONS[0] + potionStrings.DESCRIPTIONS[1] + potency + potionStrings.DESCRIPTIONS[2] + potionStrings.DESCRIPTIONS[3] + potionStrings.DESCRIPTIONS[5];
        } else {
            this.description = potionStrings.DESCRIPTIONS[0] + potionStrings.DESCRIPTIONS[3] + potionStrings.DESCRIPTIONS[4];
        }

        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

    @Override
    public AbstractPotion makeCopy() {
        return new EgoPotion();
    }
}