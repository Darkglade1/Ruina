package ruina.monsters.day49.sephirahMeltdownFlashbacks.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.watcher.EnergyDownPower;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;
import ruina.powers.MirageDexterity;
import ruina.powers.MirageStrength;

import static ruina.util.Wiz.*;

public class HodMemory extends AbstractUnremovablePower {

    public static final String POWER_ID = RuinaMod.makeID(HodMemory.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int CARD_PLAY = 5;

    public HodMemory(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        updateDescription();
        loadRegion("combust");
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.flashWithoutSound();
        ++amount;
        if (amount == CARD_PLAY) {
            amount = 0;
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractPower dex = adp().getPower(MirageDexterity.POWER_ID);
                    AbstractPower str = adp().getPower(MirageStrength.POWER_ID);
                    if(dex != null){
                        dex.amount = -dex.amount;
                        att(new RemoveSpecificPowerAction(adp(), adp(), dex));
                    }
                    if(str != null){
                        str.amount = -str.amount;
                    }
                }
            });
        }
    }

    @Override
    public void updateDescription() {
        description = String.format(DESCRIPTIONS[0], amount);
    }


    @Override
    public void onRemove() {
        super.onRemove();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractPower dex = adp().getPower(MirageDexterity.POWER_ID);
                AbstractPower str = adp().getPower(MirageStrength.POWER_ID);
                if(dex != null){
                    makePowerRemovable(dex);
                    att(new RemoveSpecificPowerAction(adp(), adp(), dex));
                }
                if(str != null){
                    makePowerRemovable(str);
                    att(new RemoveSpecificPowerAction(adp(), adp(), str));
                }
                isDone = true;
            }
        });
    }
}