package ruina.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

public class PunishingBirdPunishmentPower extends AbstractEasyPower {

    public static final String POWER_ID = RuinaMod.makeID(PunishingBirdPunishmentPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean isPunishSet = false;

    public PunishingBirdPunishmentPower(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null && info.owner != this.owner) {
            if (!isPunishSet) {
                flash();
                isPunishSet = true;
                updateDescription();
            }
        }
        return damageAmount;
    }

    public void updateDescription() {
        this.description = isPunishSet ? DESCRIPTIONS[1] : DESCRIPTIONS[0];
    }

    public boolean getPunishment() {
        return isPunishSet;
    }

    public void setPunishment(boolean v) {
        isPunishSet = v;
    }
}
