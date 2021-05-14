package ruina.monsters.day49.Angela;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.watcher.WallopAction;
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
public class LeanBloodyWings extends AbstractRuinaCard {
    public final static String ID = makeID(LeanBloodyWings.class.getSimpleName());

    private static final int COST = 2;
    private static final int DAMAGE = 16;
    private static final int BLEED = 3;
    private static final int DRAW = 1;
    private static final int ENERGY = 1;

    public LeanBloodyWings() {
        super(ID, COST, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ALL_ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_yanProtect.class.getSimpleName() + ".png"));
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = BLEED;
        secondMagicNumber = baseSecondMagicNumber = DRAW;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        doAllDmg(damage, AbstractGameAction.AttackEffect.NONE, false);
        for(AbstractMonster mo: monsterList()){ applyToTarget(mo, p, new Bleed(mo, magicNumber)); }
        doDraw(secondMagicNumber);
        atb(new GainEnergyAction(ENERGY));
    }

    public void upp() {
        upgradeBlock(3);
    }
}