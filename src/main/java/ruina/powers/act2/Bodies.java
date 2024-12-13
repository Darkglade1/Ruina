package ruina.powers.act2;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.monsters.act2.mountain.MeltedCorpses;
import ruina.monsters.act2.mountain.Mountain;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.monsterList;

public class Bodies extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Bodies.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Bodies(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void atEndOfRound() {
        boolean foundCorpse = false;
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof MeltedCorpses) {
                foundCorpse = true;
                break;
            }
        }
        if (!foundCorpse && owner instanceof Mountain) {
            ((Mountain) owner).Summon();
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0];
    }
}
