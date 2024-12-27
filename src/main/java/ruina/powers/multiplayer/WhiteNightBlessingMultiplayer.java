package ruina.powers.multiplayer;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.Wish;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.cardmods.BlockUpMod;
import ruina.cardmods.DamageUpMod;
import ruina.monsters.act3.whiteNight.WhiteNight;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.*;

public class WhiteNightBlessingMultiplayer extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(WhiteNightBlessingMultiplayer.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int maxTurns;
    WhiteNight whiteNight;

    public WhiteNightBlessingMultiplayer(AbstractCreature owner, int amount, int maxTurns, WhiteNight whiteNight) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.whiteNight = whiteNight;
        this.maxTurns = maxTurns;
        setPowerImage("WhiteNightBlessing");
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (this.amount > 0) {
            DamageUpMod damageMod = new DamageUpMod(3);
            BlockUpMod blockMod = new BlockUpMod(3);
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    if (card.baseDamage >= 0 && !(card instanceof Wish)) {
                        CardModifierManager.addModifier(card, damageMod);
                    }
                    if (card.baseBlock >= 0 && !(card instanceof Wish)) {
                        CardModifierManager.addModifier(card, blockMod);
                    }
                    this.isDone = true;
                }
            });
            applyToTarget(owner, adp(), new WhiteNightBlessingMultiplayer(owner, 1, maxTurns, whiteNight));
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount -= stackAmount;
        if (this.amount <= 0) {
            this.amount = 0;
        }
        updateDescription();
    }

    @Override
    public void duringTurn() {
        if (this.amount <= 0 || GameActionManager.turn >= maxTurns) {
            whiteNight.awaken();
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}