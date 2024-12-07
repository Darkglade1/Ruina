package ruina.powers.act3;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.act3.whiteNight.WhiteNight;
import ruina.powers.AbstractEasyPower;

public class WhiteNightBlessing extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(WhiteNightBlessing.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    WhiteNight whiteNight;

    public WhiteNightBlessing(AbstractCreature owner, int amount, WhiteNight whiteNight) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.whiteNight = whiteNight;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (this.amount > 0) {
            this.flashWithoutSound();
            this.amount--;
            if (this.amount <= 0) {
                this.amount = 0;
            }
//            FrozenMod mod = new FrozenMod();
//            atb(new AbstractGameAction() {
//                @Override
//                public void update() {
//                    if (!CardModifierManager.hasModifier(card, FrozenMod.ID)) {
//                        CardModifierManager.addModifier(card, mod.makeCopy());
//                    }
//                    this.isDone = true;
//                }
//            });
            updateDescription();
        }
    }

    @Override
    public void duringTurn() {
        if (this.amount <= 0) {
            whiteNight.awaken();
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}