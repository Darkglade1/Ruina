package ruina.monsters.act3.heart;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class HeartOfAspiration extends AbstractRuinaMonster
{
    public static final String ID = makeID(HeartOfAspiration.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte PULSATION = 0;
    private static final byte BEATS_OF_ASPIRATION = 1;

    private final int STRENGTH = calcAscensionSpecial(2);

    public HeartOfAspiration() {
        this(0.0f, 0.0f);
    }

    public HeartOfAspiration(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 280.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Heart/Spriter/Heart.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(48), calcAscensionTankiness(55));
        addMove(PULSATION, Intent.BUFF);
        addMove(BEATS_OF_ASPIRATION, Intent.ATTACK, calcAscensionDamage(14));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case PULSATION: {
                specialAnimation();
                for (AbstractMonster mo : monsterList()) {
                    applyToTarget(mo, this, new StrengthPower(mo, STRENGTH));
                }
                resetIdle();
                break;
            }
            case BEATS_OF_ASPIRATION: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        int otherEnemyCount = 0;
        for (AbstractMonster mo : monsterList()) {
            if (mo != this) {
                otherEnemyCount++;
            }
        }
        if (otherEnemyCount > 0) {
            setMoveShortcut(PULSATION);
        } else {
            setMoveShortcut(BEATS_OF_ASPIRATION);
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntBlow", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", null, this);
    }

}