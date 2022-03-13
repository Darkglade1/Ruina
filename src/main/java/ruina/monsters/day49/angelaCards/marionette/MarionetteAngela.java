package ruina.monsters.day49.angelaCards.marionette;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.cards.EGO.act3.Marionette;
import ruina.monsters.day49.Act3Angela;
import ruina.monsters.day49.Act4Angela;
import ruina.monsters.day49.angelaCards.frostsplinter.FrostSplinterAngela;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class MarionetteAngela extends AbstractRuinaCard {
    public final static String ID = makeID(MarionetteAngela.class.getSimpleName());
    private Act3Angela parent;

    public MarionetteAngela(Act3Angela parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + Marionette.class.getSimpleName() + ".png"));
        damage = baseDamage = parent.marionetteDamage;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new MarionetteAngela(parent); }
}