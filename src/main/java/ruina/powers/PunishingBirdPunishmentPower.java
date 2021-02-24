package ruina.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;

import static ruina.util.Wiz.atb;

public class PunishingBirdPunishmentPower extends AbstractEasyPower {

    public static final String POWER_ID = RuinaMod.makeID(PunishingBirdPunishmentPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean isPunishSet = false;
    private int enrageStrAmount = 7;
    public PunishingBirdPunishmentPower(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        // temp asset
        loadRegion("flight");
    }
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type != DamageInfo.DamageType.THORNS && info.type != DamageInfo.DamageType.HP_LOSS && info.owner != null && info.owner != this.owner) {
            flash();
            isPunishSet = true;
            updateDescription();
        }
        return damageAmount;
    }
    public void atStartOfTurnPostDraw() {
        isPunishSet = false;
        updateDescription();
    }
    public void updateDescription(){ this.description = isPunishSet ? String.format(DESCRIPTIONS[1], enrageStrAmount) : String.format(DESCRIPTIONS[0], enrageStrAmount); }
    public boolean getPunishment(){ return isPunishSet; }
    public void setPunishment(boolean v){ isPunishSet = v; }
    public void onRemove(){ atb(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, enrageStrAmount))); }
}
