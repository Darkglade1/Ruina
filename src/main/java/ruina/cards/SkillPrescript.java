package ruina.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_yanProtect;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class SkillPrescript extends AbstractRuinaCard {
    public final static String ID = makeID(SkillPrescript.class.getSimpleName());

    public SkillPrescript() {
        super(ID, 0, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF, CardColor.COLORLESS, makeImagePath("cards/" + CHRBOSS_yanProtect.class.getSimpleName() + ".png"));
        baseBlock = 4;
        selfRetain = true;
        exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        blck();
    }

    public void upp() {
        upgradeBlock(3);
    }
}