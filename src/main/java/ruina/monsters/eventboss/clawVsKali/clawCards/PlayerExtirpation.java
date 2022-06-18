package ruina.monsters.eventboss.clawVsKali.clawCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.theHead.Baral;
import ruina.monsters.theHead.baralCards.Extirpation;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class PlayerExtirpation extends AbstractRuinaCard {
    public final static String ID = makeID(PlayerExtirpation.class.getSimpleName());
    private final Baral parent;

    public PlayerExtirpation(Baral parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + Extirpation.class.getSimpleName() + ".png"));
        damage = baseDamage = 24;
        block = baseBlock = 10;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        parent.pierceAnimation(m);
        parent.waitAnimation(0.5f);
        parent.pierceFinAnimation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        parent.resetIdle(0.75f);
        blck();
    }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new PlayerExtirpation(parent);
    }
}