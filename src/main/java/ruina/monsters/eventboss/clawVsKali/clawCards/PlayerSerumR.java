package ruina.monsters.eventboss.clawVsKali.clawCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.theHead.Baral;
import ruina.monsters.theHead.baralCards.SerumR;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class PlayerSerumR extends AbstractRuinaCard {
    public final static String ID = makeID(PlayerSerumR.class.getSimpleName());
    private final Baral parent;

    public PlayerSerumR(Baral parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + SerumR.class.getSimpleName() + ".png"));
        damage = baseDamage = 22;
        block = baseBlock = 15;
        magicNumber = baseMagicNumber = 2;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        float initialX = parent.drawX;
        float targetBehind = m.drawX + 150.0f * Settings.scale;
        parent.serumR1(m);
        parent.waitAnimation(0.5f);
        parent.moveSpriteAnimation(targetBehind, m);
        parent.serumR2(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        parent.waitAnimation(0.75f);
        parent.setFlipAnimation(false, m);
        parent.serumR1(m);
        parent.waitAnimation(0.5f);
        parent.moveSpriteAnimation(initialX, m);
        parent.serumR2(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        parent.resetIdle(0.75f);
        parent.setFlipAnimation(true, m);
        blck();
    }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new PlayerSerumR(parent);
    }
}