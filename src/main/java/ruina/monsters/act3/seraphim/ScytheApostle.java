package ruina.monsters.act3.seraphim;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Apostles;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.WingsOfGrace;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.adp;

public class ScytheApostle extends AbstractRuinaMonster
{
    public static final String ID = makeID(ScytheApostle.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    private InvisibleBarricadePower power = new InvisibleBarricadePower(this);

    private static final byte FOLLOW_THEE = 0;
    private static final byte THY_WILL_BE_DONE = 1;
    private static final byte PRESERVE_THEE = 2;
    private static final byte TEACH_US = 3;
    private int followTheeDamage = calcAscensionDamage(8);
    private int followTheeBlock = calcAscensionTankiness(8);
    private int thyWillBeDoneDamage = calcAscensionDamage(4);
    private int teachUsWings = calcAscensionSpecial(1);
    private int preserveTheeBlock = calcAscensionTankiness(7);
    private int startingState;
    private ParadiseLost paradiseLost;
    public ScytheApostle(final float x, final float y, ParadiseLost parent, int startingState) {
        super(NAME, ID, 75, -5.0F, 0, 130.0f, 125.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ScytheApostle/Spriter/ScytheApostle.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(FOLLOW_THEE, Intent.ATTACK_DEFEND, followTheeDamage);
        addMove(THY_WILL_BE_DONE, Intent.ATTACK, thyWillBeDoneDamage, 2, true);
        addMove(PRESERVE_THEE, Intent.DEFEND);
        addMove(TEACH_US, Intent.BUFF);
        this.startingState = startingState;
        paradiseLost = parent;
        firstMove = true;
    }

    @Override
    public void takeTurn()
    {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;
        if(info.base > -1) { info.applyPowers(this, adp()); }
        switch (nextMove){
            case TEACH_US:
                atb(new ApplyPowerAction(this, this, new WingsOfGrace(this, teachUsWings)));
                break;
            case THY_WILL_BE_DONE:
                for (int i = 0; i < multiplier; i++) { dmg(adp(), info); }
                break;
            case PRESERVE_THEE:
                for(AbstractMonster m: monsterList()){ atb(new GainBlockAction(m, preserveTheeBlock)); }
                break;
            case FOLLOW_THEE:
                dmg(adp(), info);
                atb(new GainBlockAction(this, followTheeBlock));
                break;
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                firstMove = false;
                isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        switch (startingState){
            case 0:
                if(lastMove(TEACH_US)){ setMoveShortcut( num <= 45 ? FOLLOW_THEE : THY_WILL_BE_DONE, num <= 45 ? MOVES[FOLLOW_THEE] : MOVES[THY_WILL_BE_DONE]); }
                else { setMoveShortcut(TEACH_US, MOVES[TEACH_US]); }
                break;
            case 1:
                if(lastMove(FOLLOW_THEE)){ setMoveShortcut(PRESERVE_THEE, MOVES[PRESERVE_THEE]); }
                else { setMoveShortcut(FOLLOW_THEE, MOVES[FOLLOW_THEE]); }
                break;
            case 2:
                if(lastMove(THY_WILL_BE_DONE)){ setMoveShortcut(num <= 45 ? TEACH_US : THY_WILL_BE_DONE, num <= 45 ? MOVES[TEACH_US] : MOVES[THY_WILL_BE_DONE]); }
                else { setMoveShortcut(THY_WILL_BE_DONE, MOVES[THY_WILL_BE_DONE]); }
                break;
            case 3:
                if (lastMove(TEACH_US)) { setMoveShortcut(THY_WILL_BE_DONE, MOVES[THY_WILL_BE_DONE]);
                } else if (lastMove(PRESERVE_THEE)) { setMoveShortcut(TEACH_US, MOVES[TEACH_US]);
                } else { setMoveShortcut(PRESERVE_THEE, MOVES[PRESERVE_THEE]); }
                break;
        }
    }


    @Override
    public void usePreBattleAction() {  }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        paradiseLost.ApostleKillCounter -= 1;
        AbstractPower p = paradiseLost.getPower(Apostles.POWER_ID);
        if(p != null){
            if(p.amount - 1 == 0){ atb(new RemoveSpecificPowerAction(paradiseLost, paradiseLost, p)); }
            else { atb(new ReducePowerAction(paradiseLost, paradiseLost, p, 1)); }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                paradiseLost.rollMove();
                paradiseLost.createIntent();
                this.isDone = true;
            }
        });
    }

}