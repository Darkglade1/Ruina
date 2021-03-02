package ruina.monsters.eventBoss.bosses.redmist.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaCardMonster;
import ruina.monsters.eventBoss.core.AbstractRuinaBossCard;
import ruina.monsters.eventBoss.core.power.EnemyDrawPower;

import static ruina.RuinaMod.*;

@AutoAdd.Ignore
public class CHR_UpstandingSlash extends AbstractRuinaBossCard {
    public final static String ID = makeID(CHR_UpstandingSlash.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String IMG_PATH = "UpstandingSlash";
    private static int COST = 2;
    private int DAMAGE = 7;
    private int UPG_DAMAGE = 2;
    private int HITS = 2;
    private int DRAW = 1;
    private int THRESHOLD = 8;


    public CHR_UpstandingSlash() {
        super(ID, cardStrings.NAME, IMG_PATH, COST, cardStrings.DESCRIPTION, CardType.ATTACK, CardColor.RED, CardRarity.RARE, CardTarget.NONE, AbstractMonster.Intent.ATTACK, true);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = HITS;
        isMultiDamage = true;
        intentMultiAmt = magicNumber;
    }

    @Override
    public void upgrade() {

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        final int[] damageThreshold = {0};
        for(int i = 0; i < magicNumber; i+= 1){
            DamageInfo info = new DamageInfo(m, damage, damageTypeForTurn);
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    target = p;
                    if (this.target != null) {
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.SLASH_HEAVY));
                        this.target.damage(info);
                        damageThreshold[0] += target.lastDamageTaken;
                    }
                    this.isDone = true;
                }
            });
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if(damageThreshold[0] >= THRESHOLD){
                    atb(new ApplyPowerAction(m, m, new EnemyDrawPower(m, DRAW)));
                    for(AbstractCard c: AbstractRuinaCardMonster.boss.chosenArchetype.getHandPile().group){
                        if(c instanceof CHR_UpstandingSlash) { c.modifyCostForCombat(-1); }
                    }
                    for(AbstractCard c: AbstractRuinaCardMonster.boss.chosenArchetype.getDraw().group){
                        if(c instanceof CHR_UpstandingSlash) { c.modifyCostForCombat(-1); }
                    }
                    for(AbstractCard c: AbstractRuinaCardMonster.boss.chosenArchetype.getDiscard().group){
                        if(c instanceof CHR_UpstandingSlash) { c.modifyCostForCombat(-1); }
                    }
                }
                isDone = true;
            }
        });
    }

    @Override
    public AbstractCard makeCopy() {
        return new CHR_UpstandingSlash();
    }
}
