package ruina.monsters.uninvitedGuests.extra.phillip.cards.malkuth;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.watcher.WallopAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class CHRBOSS_NostalgicEmbrace extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_NostalgicEmbrace.class.getSimpleName());
    public static final int DAMAGE = 20;
    public static final int UPG_DAMAGE = 5;

    public CHRBOSS_NostalgicEmbrace() {
        super(ID, 2, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = DAMAGE;
        tags.add(RuinaMod.Enums.ABNO_HTB);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new WallopAction(m,  new DamageInfo(AbstractDungeon.player, damage, damageTypeForTurn)));
    }

    @Override
    public void upp() {
        upgradeDamage(UPG_DAMAGE);
    }

    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_NostalgicEmbrace(); }
}