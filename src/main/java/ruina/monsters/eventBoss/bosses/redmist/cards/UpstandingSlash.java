package ruina.monsters.eventBoss.bosses.redmist.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import jdk.nashorn.internal.ir.annotations.Ignore;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.cards.EGO.act3.Penitence;
import ruina.monsters.AbstractRuinaCardMonster;
import ruina.monsters.eventBoss.core.AbstractRuinaBossCard;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

@AutoAdd.Ignore
public class UpstandingSlash extends AbstractRuinaBossCard {
    public final static String ID = makeID(UpstandingSlash.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String IMG_PATH = makeCardPath(UpstandingSlash.class.getSimpleName() + ".png");
    private static int COST = 2;
    private int COST_DECREASE = -1;
    private int DAMAGE = 6;
    private int UPG_DAMAGE = 2;
    private int HITS = 2;
    private int DRAW = 1;
    private int THRESHOLD = 8;


    public UpstandingSlash() {
        super(ID, cardStrings.NAME, IMG_PATH, COST, cardStrings.DESCRIPTION, CardType.ATTACK, RuinaMod.Enums.EGO, CardRarity.RARE, CardTarget.NONE, AbstractMonster.Intent.ATTACK);
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
                    // draw shit next turn
                    // fix static dumb
                    for(AbstractCard c: AbstractRuinaCardMonster.boss.hand.group){
                        if(c.cost - 1 < 0){ c.cost = 0; }
                        else { c.cost -= 1; }
                        c.isCostModified = true;
                    }
                    for(AbstractCard c: AbstractRuinaCardMonster.boss.drawPile.group){
                        if(c.cost - 1 < 0){ c.cost = 0; }
                        else { c.cost -= 1; }
                        c.isCostModified = true;
                    }
                    for(AbstractCard c: AbstractRuinaCardMonster.boss.discardPile.group){
                        if(c.cost - 1 < 0){ c.cost = 0; }
                        else { c.cost -= 1; }
                        c.isCostModified = true;
                    }
                }
            }
        });
    }

    @Override
    public AbstractCard makeCopy() {
        return null;
    }
}
