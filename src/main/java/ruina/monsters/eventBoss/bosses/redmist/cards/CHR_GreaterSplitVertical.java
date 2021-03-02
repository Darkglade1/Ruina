package ruina.monsters.eventBoss.bosses.redmist.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaCardMonster;
import ruina.monsters.eventBoss.core.AbstractRuinaBossCard;
import ruina.monsters.eventBoss.core.power.EnemyEnergizedPower;
import ruina.powers.Bleed;

import static ruina.RuinaMod.makeCardPath;
import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHR_GreaterSplitVertical extends AbstractRuinaBossCard {
    public final static String ID = makeID(CHR_GreaterSplitVertical.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String IMG_PATH = "GreaterSplitVertical";
    private static int COST = 4;
    private int DAMAGE = 30;
    private int UPG_DAMAGE = 5;
    private int BLEED = 3;

    public CHR_GreaterSplitVertical() {
        super(ID, cardStrings.NAME, IMG_PATH, COST, cardStrings.DESCRIPTION, CardType.ATTACK, RuinaMod.Enums.EGO, CardRarity.RARE, CardTarget.NONE, AbstractMonster.Intent.ATTACK, true);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = BLEED;
    }

    @Override
    public void upgrade() {

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                int damageThreshold = 0;
                for(int i = 0; i < magicNumber; i += 1){
                    DamageInfo damageInfo = new DamageInfo(m, damage, damageTypeForTurn);
                    p.damage(damageInfo);
                    damageThreshold += p.lastDamageTaken;
                }
                if(damageThreshold > 0){ atb(new ApplyPowerAction(m, m, new Bleed(p, magicNumber, true))); }
            }
        });
    }

    @Override
    public AbstractCard makeCopy() { return new CHR_GreaterSplitVertical(); }
}
