package ruina.monsters.uninvitedGuests.normal.argalia.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;
import ruina.monsters.uninvitedGuests.normal.bremen.bremenCards.Bawk;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.dmg;

@AutoAdd.Ignore
public class CHRALLY_FURIOSO extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_FURIOSO.class.getSimpleName());
    private final Roland parent;

    public CHRALLY_FURIOSO(Roland parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.furiosoDamage;
        magicNumber = baseMagicNumber = parent.furiosoHits;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        float initialX = parent.drawX;
        float targetBehind = m.drawX + 150.0f * Settings.scale;
        float targetFront = m.drawX - 200.0f * Settings.scale;
        parent.gun1Animation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.gun2Animation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.moveAnimation(targetBehind, m);
        parent.pierceAnimation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.setFlipAnimation(true, m);
        parent.attackAnimation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.moveAnimation(targetFront, m);
        parent.knifeAnimation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.setFlipAnimation(false, m);
        parent.mook1Animation(m);
        parent.waitAnimation(0.15f);
        parent.mook2Animation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.moveAnimation(targetBehind, m);
        parent.claw1Animation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.moveAnimation(targetFront, m);
        parent.setFlipAnimation(true, m);
        parent.claw2Animation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.setFlipAnimation(false, m);
        parent.club1Animation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.club2Animation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.wheelsAnimation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.slashAnimation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.gun3Animation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.sword1Animation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.sword2Animation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        waitAnimation(m);
        parent.sword3Animation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        parent.resetIdle();
        parent.moveAnimation(initialX, null);
        parent.setFlipAnimation(false, null);
    }
    
    protected void waitAnimation(AbstractCreature enemy) {
        parent.waitAnimation(0.25f, enemy);
    }

    @Override
    public void upp() {
    }

    @Override
    public AbstractCard makeCopy() {
        return new CHRALLY_FURIOSO(parent);
    }
}