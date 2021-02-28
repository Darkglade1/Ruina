package ruina.monsters.act3.priceOfSilence;

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

public class RemnantOfTime extends AbstractRuinaMonster
{
    public static final String ID = makeID(RemnantOfTime.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte BACKLASH_OF_TIME = 0;
    private static final byte TORRENT_OF_HOURS = 1;

    private final int BLOCK = calcAscensionTankiness(9);
    private final int STRENGTH = calcAscensionSpecial(2);

    public RemnantOfTime() {
        this(0.0f, 0.0f);
    }

    public RemnantOfTime(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 280.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Remnant/Spriter/Remnant.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(88), calcAscensionTankiness(94));
        addMove(BACKLASH_OF_TIME, Intent.ATTACK, calcAscensionDamage(7), 2, true);
        addMove(TORRENT_OF_HOURS, Intent.DEFEND_BUFF);
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case BACKLASH_OF_TIME: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        attackForwardAnimation(adp());
                    } else {
                        attackBackAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case TORRENT_OF_HOURS: {
                blockAnimation();
                for (AbstractMonster mo : monsterList()) {
                    block(mo, BLOCK);
                    applyToTargetNextTurn(mo, new StrengthPower(mo, STRENGTH));
                }
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(BACKLASH_OF_TIME)) {
            setMoveShortcut(TORRENT_OF_HOURS, MOVES[TORRENT_OF_HOURS]);
        } else {
            setMoveShortcut(BACKLASH_OF_TIME, MOVES[BACKLASH_OF_TIME]);
        }
    }

    private void attackForwardAnimation(AbstractCreature enemy) {
        animationAction("AttackForward", "BluntBlow", enemy, this);
    }

    private void attackBackAnimation(AbstractCreature enemy) {
        animationAction("AttackBack", "Slash", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

}