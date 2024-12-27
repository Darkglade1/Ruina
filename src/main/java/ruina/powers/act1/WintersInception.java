package ruina.powers.act1;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.cardmods.LaurelWreathMod;
import ruina.powers.AbstractUnremovablePower;

public class WintersInception extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(WintersInception.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public WintersInception(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    boolean triggered = false;

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        if (type == DamageInfo.DamageType.NORMAL && triggered && (card == null || !CardModifierManager.hasModifier(card, LaurelWreathMod.ID))) {
            return damage * (1.0f - ((float)amount / 100));
        } else {
            return damage;
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (!CardModifierManager.hasModifier(card, LaurelWreathMod.ID)) {
            if (!triggered && card.type == AbstractCard.CardType.ATTACK) {
                triggered = true;
                this.flash();
                LaurelWreathMod mod = new LaurelWreathMod();
                CardModifierManager.addModifier(card, mod.makeCopy());
            }
        }
    }

    @Override
    public void atEndOfRound() {
        triggered = false;
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
    }
}
