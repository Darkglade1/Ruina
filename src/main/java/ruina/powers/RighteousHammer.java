package ruina.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.day49.act5.Act5Angela1;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.att;

public class RighteousHammer extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(RighteousHammer.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public RighteousHammer(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        loadRegion("combust");
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner == this.owner) {
            this.flash();
            att(new ApplyPowerAction(adp(), adp(), new BluntVulnerability(adp(), 25, (Act5Angela1) this.owner)));
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

}