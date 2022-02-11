package ruina.cards.EGO.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.act3.JudgementBird;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class Justitia extends AbstractEgoCard {
    public final static String ID = makeID(Justitia.class.getSimpleName());

    public static final int KILL_THRESHOLD = 30;
    public static final int HP_LOSS = 11;

    public Justitia() {
        super(ID, 1, CardType.SKILL, CardTarget.ENEMY);
        magicNumber = baseMagicNumber = HP_LOSS;
        isEthereal = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                float hpThreshold = (float)KILL_THRESHOLD / 100;
                if (m.currentHealth <= m.maxHealth * hpThreshold) {
                    AbstractRuinaMonster.playSound("JudgementDing");
                    JudgementBird.judgementVfx();
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            m.currentHealth = 0;
                            m.healthBarUpdatedEvent();
                            m.useStaggerAnimation();
                            AbstractDungeon.effectList.add(new StrikeEffect(m, m.hb.cX, m.hb.cY, 999));
                            m.damage(new DamageInfo(null, 0, DamageInfo.DamageType.HP_LOSS));
                            this.isDone = true;
                        }
                    });
                } else {
                    atb(new LoseHPAction(m, p, magicNumber));
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
        //changes description when player is hovering monster to show kill threshold
        if (mo != null) {
            float hpThreshold = (float)KILL_THRESHOLD / 100;
            int monsterThreshold = (int)(mo.maxHealth * hpThreshold);
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0] + monsterThreshold + cardStrings.EXTENDED_DESCRIPTION[1];
            this.initializeDescription();
        }
    }

    @Override
    public void update() {
        super.update();
        //changes description back when player is not hovering monster
        this.rawDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void upp() {
        isEthereal = false;
    }
}