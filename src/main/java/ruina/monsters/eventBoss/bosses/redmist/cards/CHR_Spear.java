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
import ruina.monsters.eventBoss.core.power.EnemyDrawPower;

import static ruina.RuinaMod.makeCardPath;
import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHR_Spear extends AbstractRuinaBossCard {
    public final static String ID = makeID(CHR_Spear.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String IMG_PATH = "Spear";
    private static int COST = 2;
    private int DAMAGE = 3;
    private int UPG_DAMAGE = 2;
    private int HITS = 3;
    private int DRAW = 1;
    private int THRESHOLD = 8;


    public CHR_Spear() {
        super(ID, cardStrings.NAME, IMG_PATH, COST, cardStrings.DESCRIPTION, CardType.ATTACK, CardColor.RED, CardRarity.RARE, CardTarget.NONE, AbstractMonster.Intent.ATTACK, true);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = HITS;
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
                if(damageThreshold >= THRESHOLD){
                    atb(new ApplyPowerAction(m, m, new EnemyDrawPower(m, DRAW)));
                    for(AbstractCard c: AbstractRuinaCardMonster.boss.hand.group){
                        if(c instanceof CHR_Spear) {
                            if (c.cost - 1 < 0) { c.cost = 0;
                            } else { c.cost -= 1; }
                            c.isCostModified = true;
                        }
                    }
                    for(AbstractCard c: AbstractRuinaCardMonster.boss.drawPile.group){
                        if(c instanceof CHR_Spear) {
                            if (c.cost - 1 < 0) { c.cost = 0;
                            } else { c.cost -= 1; }
                            c.isCostModified = true;
                        }
                    }
                    for(AbstractCard c: AbstractRuinaCardMonster.boss.discardPile.group){
                        if(c instanceof CHR_Spear) {
                            if (c.cost - 1 < 0) { c.cost = 0;
                            } else { c.cost -= 1; }
                            c.isCostModified = true;
                        }
                    }
                }
            }
        });
    }

    @Override
    public AbstractCard makeCopy() {
        return new CHR_Spear();
    }
}
