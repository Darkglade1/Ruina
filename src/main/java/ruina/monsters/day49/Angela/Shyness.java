package ruina.monsters.day49.Angela;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.EnergizedPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_yanProtect;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.block;
import static ruina.util.actionShortcuts.doPow;

@AutoAdd.Ignore
public class Shyness extends AbstractRuinaCard {
    public final static String ID = makeID(Shyness.class.getSimpleName());

    private static final int COST = 1;
    private static final int BLOCK = 8;
    private static final int ENERGY = 3;

    public Shyness() {
        super(ID, COST, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_yanProtect.class.getSimpleName() + ".png"));
        block = baseBlock = BLOCK;
        magicNumber = baseMagicNumber = ENERGY;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        block(p, block);
        doPow(p, new EnergizedPower(p, magicNumber));
        // add energy
    }

    public void upp() {
        upgradeBlock(3);
    }
}