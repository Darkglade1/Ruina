package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.cards.EGO.AbstractEgoCard;

import static com.evacipated.cardcrawl.mod.stslib.StSLib.getMasterDeckEquivalent;
import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class DaCapo extends AbstractEgoCard {
    public final static String ID = makeID(DaCapo.class.getSimpleName());
    public static final int DAMAGE = 8;
    public static final int UP_DAMAGE = 2;
    public static final int MAX_UPGRADES = 5;
    public static final int VULNERABLE = 2;

    public DaCapo() {
        this(0);
    }

    public DaCapo(int upgrades) {
        super(ID, 1, CardType.ATTACK, CardTarget.ALL_ENEMY);
        damage = baseDamage = DAMAGE;
        secondMagicNumber = baseSecondMagicNumber = MAX_UPGRADES;
        magicNumber = baseMagicNumber = VULNERABLE;
        isMultiDamage = true;
        this.timesUpgraded = upgrades;
        exhaust = true;
        setText();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        allDmg(AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        if (timesUpgraded >= MAX_UPGRADES) {
            for (AbstractMonster mo : monsterList()) {
                applyToTarget(mo, adp(), new VulnerablePower(mo, magicNumber, false));
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (canUpgrade()) {
                    //upgrade master deck copy
                    AbstractCard c = getMasterDeckEquivalent(DaCapo.this);
                    if (c != null) {
                        c.upgrade();
                        AbstractDungeon.player.bottledCardUpgradeCheck(c);
                    }
                    //upgrade itself
                    upgrade();
                    AbstractDungeon.player.bottledCardUpgradeCheck(DaCapo.this);
                }
                isDone = true;
            }
        });
    }

    public boolean canUpgrade() {
        return timesUpgraded < MAX_UPGRADES;
    }

    public void upgrade() {
        if (canUpgrade()) {
            upgradeDamage(UP_DAMAGE + timesUpgraded);
            this.upgraded = true;
            this.timesUpgraded++;
            if (this.timesUpgraded >= MAX_UPGRADES) {
                isInnate = true;
            }
            setText();
        }
    }

    @Override
    public void upp() {

    }

    private void setText() {
        if (timesUpgraded >= MAX_UPGRADES) {
            uDesc();
            timesUpgraded = MAX_UPGRADES;
        }
        if (timesUpgraded > 0) {
            name = cardStrings.EXTENDED_DESCRIPTION[timesUpgraded - 1];
            this.initializeTitle();
        }
    }
}