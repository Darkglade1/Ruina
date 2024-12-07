package ruina.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class Gift extends AbstractRuinaCard {
    public final static String ID = makeID(Gift.class.getSimpleName());
    private static final int DAMAGE = 5;
    private static final int UPG_DAMAGE = 3;

    public Gift() {
        super(ID, -2, CardType.STATUS, CardRarity.SPECIAL, CardTarget.NONE, CardColor.COLORLESS);
        magicNumber = baseMagicNumber = DAMAGE;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            atb(new DamageAction(adp(), new DamageInfo(adp(), magicNumber, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.POISON));
        }
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        this.dontTriggerOnUseCard = true;
        AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
    }

    public void upp() {
        upgradeMagicNumber(UPG_DAMAGE);
    }
}