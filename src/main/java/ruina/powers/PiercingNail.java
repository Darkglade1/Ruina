package ruina.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.day49.act5.Act5Angela1;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.att;

public class PiercingNail extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(PiercingNail.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public PiercingNail(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        loadRegion("combust");
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner == this.owner) {
            this.flash();
            att(new ApplyPowerAction(adp(), adp(), new PierceVulnerability(adp(), 25, (Act5Angela1) this.owner)));
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

}