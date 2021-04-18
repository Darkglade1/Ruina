package ruina.monsters.uninvitedGuests.extra.philip.powers;

import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import org.omg.CORBA.PRIVATE_MEMBER;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;

import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public class FairyFestival extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(FairyFestival.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final int STR_GAIN = 3;
    public FairyFestival(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, STR_GAIN);
    }

    @Override
    public void updateDescription() { this.description = String.format(DESCRIPTIONS[0], amount); }

    public void atStartOfTurnPostDraw(){
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if(owner.currentHealth <= owner.maxHealth / 2){
                    flash();
                    att(new ApplyPowerAction(owner, owner, new StrengthPower(owner, STR_GAIN)));
                }
                isDone = true;
            }
        });
    }
}