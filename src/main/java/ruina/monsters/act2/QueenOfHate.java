package ruina.monsters.act2;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Hysteria;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class QueenOfHate extends AbstractRuinaMonster
{
    public static final String ID = makeID(QueenOfHate.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte ARCANA_BEATS = 0;
    private static final byte NAME_OF_HATE = 1;
    private static final byte LIGHT_OF_HATRED = 2;

    private final int WEAK_AMT = calcAscensionSpecial(2);
    private final int FRAIL_AMT = calcAscensionSpecial(2);
    private final int POWER_THRESHOLD = 3;
    private final int BURN = calcAscensionSpecial(1);
    private final int VULNERABLE = calcAscensionSpecial(3);

    public boolean hysteriaTriggered = false;
    private boolean usedLight = false;

    public QueenOfHate() {
        this(0.0f, 0.0f);
    }

    public QueenOfHate(final float x, final float y) {
        super(NAME, ID, 40, -5.0F, 0, 300.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("QueenOfHate/Spriter/QueenOfHate.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(101), calcAscensionTankiness(108));
        addMove(ARCANA_BEATS, Intent.ATTACK, calcAscensionDamage(19));
        addMove(NAME_OF_HATE, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
        addMove(LIGHT_OF_HATRED, Intent.DEBUFF);
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new Hysteria(this, WEAK_AMT, FRAIL_AMT, POWER_THRESHOLD, this));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        if (this.firstMove) {
            firstMove = false;
        }

        switch (this.nextMove) {
            case ARCANA_BEATS: {
                biteAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case NAME_OF_HATE: {
                shootAnimation(adp());
                dmg(adp(), info);
                intoDiscardMo(new Burn(), BURN, this);
                resetIdle();
                break;
            }
            case LIGHT_OF_HATRED: {
                applyToTarget(adp(), this, new VulnerablePower(adp(), VULNERABLE, true));
                usedLight = true;
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.hysteriaTriggered && !usedLight) {
            setMoveShortcut(LIGHT_OF_HATRED, MOVES[LIGHT_OF_HATRED]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(ARCANA_BEATS)) {
                possibilities.add(ARCANA_BEATS);
            }
            if (!this.lastTwoMoves(NAME_OF_HATE)) {
                possibilities.add(NAME_OF_HATE);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    private void biteAnimation(AbstractCreature enemy) {
        animationAction("Bite", "MagicSnakeAtk", enemy, this);
    }

    private void shootAnimation(AbstractCreature enemy) {
        animationAction("Shoot", "MagicGun", enemy, this);
    }

}