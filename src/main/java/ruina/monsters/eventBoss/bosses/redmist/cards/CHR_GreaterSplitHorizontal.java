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
import ruina.monsters.eventBoss.core.AbstractRuinaBossCard;
import ruina.powers.Bleed;

import static ruina.RuinaMod.makeCardPath;
import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTargetTop;

@AutoAdd.Ignore
public class CHR_GreaterSplitHorizontal extends AbstractRuinaBossCard {
    public final static String ID = makeID(CHR_GreaterSplitHorizontal.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String IMG_PATH = "GreaterSplitHorizontal";
    private static int COST = 4;
    private int DAMAGE = 40;
    private int UPG_DAMAGE = 5;
    private int BLEED = 5;

    public CHR_GreaterSplitHorizontal() {
        super(ID, cardStrings.NAME, IMG_PATH, COST, cardStrings.DESCRIPTION, CardType.ATTACK, RuinaMod.Enums.EGO, CardRarity.RARE, CardTarget.NONE, AbstractMonster.Intent.ATTACK, true);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = BLEED;
        this.exhaust = true;
    }

    @Override
    public void upgrade() {

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        final int[] damageThreshold = {0};
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
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if(damageThreshold[0] > 0){ applyToTargetTop(p, m, new Bleed(p, BLEED)); }
                isDone = true;
            }
        });
    }

    @Override
    public AbstractCard makeCopy() { return new CHR_GreaterSplitVertical(); }
}