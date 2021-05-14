package ruina.monsters.day49.Angela;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_yanProtect;
import ruina.powers.Bleed;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;
import static ruina.util.Wiz.*;
import static ruina.util.actionShortcuts.doAllDmg;
import static ruina.util.actionShortcuts.doDraw;

@AutoAdd.Ignore
public class Coffin extends AbstractRuinaCard {
    public final static String ID = makeID(Coffin.class.getSimpleName());

    private static final int COST = 4;
    private static final int DAMAGE = 10;

    public Coffin() {
        super(ID, COST, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ALL_ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_yanProtect.class.getSimpleName() + ".png"));
        damage = baseDamage = DAMAGE;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                doAllDmg(m.currentBlock, DamageInfo.DamageType.THORNS, AttackEffect.NONE, true);
                m.loseBlock();
                isDone = true;
            }
        });
    }

    public void upp() { }
}