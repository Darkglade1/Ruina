package ruina.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

public class BlackForest extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(BlackForest.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BlackForest(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        priority = 99;
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if(owner instanceof ruina.monsters.act3.Twilight && ((ruina.monsters.act3.Twilight) owner).eggBrokenRecently){
            return 0;
        }
        return super.onAttackedToChangeDamage(info, damageAmount);
    }

    @Override
    public void updateDescription() {
        if(owner instanceof ruina.monsters.act3.Twilight && ((ruina.monsters.act3.Twilight) owner).eggBrokenRecently){description = DESCRIPTIONS[1];}
        else {description = DESCRIPTIONS[0];}
    }
}