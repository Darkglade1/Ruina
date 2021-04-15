package ruina.monsters.uninvitedGuests.extra.phillip.cards.malkuth;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class CHRBOSS_RoughLoving extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_RoughLoving.class.getSimpleName());
    public static final int BLOCK = 12;
    public static final int STR_DOWN = 2;
    public static final int UPG_BLOCK = 4;

    public CHRBOSS_RoughLoving() {
        super(ID, 2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        block = baseBlock = BLOCK;
        magicNumber = baseMagicNumber = STR_DOWN;
        tags.add(RuinaMod.Enums.ABNO_HTB);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new GainBlockAction(p, block));
        applyToTarget(m,m, new StrengthPower(m, -magicNumber));
        if(m.hasPower(ArtifactPower.POWER_ID)){ applyToTarget(m,m, new GainStrengthPower(m, magicNumber)); }
    }

    @Override
    public void upp() { upgradeBlock(UPG_BLOCK); }


    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_RoughLoving(); }
}