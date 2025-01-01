package ruina.powers.act2;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cardmods.DrawReductionMod;
import ruina.cardmods.EnergyLossMod;
import ruina.cardmods.VulnerableMod;
import ruina.cards.FalsePresent;
import ruina.monsters.act2.Oz.ScowlingFace;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.adp;

public class Emerald extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Emerald.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Emerald(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void onDeath() {
        boolean shouldTrigger = true;
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof ScowlingFace && !mo.isDeadOrEscaped()) {
                shouldTrigger = false;
                break;
            }
        }
        if (shouldTrigger) {
            for (AbstractCard card : adp().hand.group) {
                if (card instanceof FalsePresent) {
                    if (CardModifierManager.hasModifier(card, EnergyLossMod.ID)) {
                        CardModifierManager.removeModifiersById(card, EnergyLossMod.ID, false);
                        card.flash();
                    }
                    if (CardModifierManager.hasModifier(card, DrawReductionMod.ID)) {
                        CardModifierManager.removeModifiersById(card, DrawReductionMod.ID, false);
                        card.flash();
                    }
                    if (CardModifierManager.hasModifier(card, VulnerableMod.ID)) {
                        CardModifierManager.removeModifiersById(card, VulnerableMod.ID, false);
                        card.flash();
                    }
                }
            }
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0];
    }
}
