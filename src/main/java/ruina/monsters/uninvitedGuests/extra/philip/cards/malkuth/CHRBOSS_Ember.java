package ruina.monsters.uninvitedGuests.extra.philip.cards.malkuth;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.FreeAttackPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToSelf;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class CHRBOSS_Ember extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_Ember.class.getSimpleName());
    public static final int BLOCK = 8;
    public static final int UPG_BLOCK = 4;

    public CHRBOSS_Ember() {
        super(ID, 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        block = baseBlock = BLOCK;
        tags.add(RuinaMod.Enums.ABNO_SG);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new GainBlockAction(p, block));
        applyToSelf(new FreeAttackPower(p, 1));
    }

    @Override
    public void upp() { upgradeBlock(UPG_BLOCK); }


    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_Ember(); }
}