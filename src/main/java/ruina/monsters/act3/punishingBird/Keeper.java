package ruina.monsters.act3.punishingBird;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.BetterSpriterAnimation;
import ruina.cards.ForestKeeperLock;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.act2.BadWolf;
import ruina.monsters.act3.seraphim.Prophet;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Paralysis;
import ruina.powers.PunishingBirdPunishmentPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.atb;

public class Keeper extends AbstractRuinaMonster {
    public static final String ID = makeID(Keeper.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte CUCKOO = 0;
    private static final byte RING = 1;
    private static final byte SMACK = 2;

    public static final String lock_pid = makeID("Lock");
    public static final PowerStrings str_lock = CardCrawlGame.languagePack.getPowerStrings(lock_pid);
    public static final String lock_pname = str_lock.NAME;
    public static final String[] lock_desc = str_lock.DESCRIPTIONS;
    private final PunishingBird bird;

    public Keeper(final float x, final float y, PunishingBird parent) {
        super(NAME, ID, 500, -5.0F, 0, 160.0f, 185.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ScytheApostle/Spriter/ScytheApostle.scml"));
        this.type = EnemyType.ELITE;
        setHp(calcAscensionTankiness(70), calcAscensionTankiness(75));
        addMove(CUCKOO, Intent.DEFEND);
        addMove(RING, Intent.ATTACK, calcAscensionDamage(5), 3, true);
        addMove(SMACK, Intent.ATTACK_DEBUFF, calcAscensionDamage(10));
        bird = parent;
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;
        if (info.base > -1) {
            info.applyPowers(this, adp());
        }
        switch (nextMove) {
            case CUCKOO:
                block(this, calcAscensionTankiness(12));
                break;
            case RING:
                for (int i = 0; i < multiplier; i++) { dmg(adp(), info); }
                break;
            case SMACK:
                dmg(adp(), info);
                atb(new ApplyPowerAction(adp(), this, new Paralysis(adp(), calcAscensionSpecial(1))));
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if(lastMove(CUCKOO)){
            if(num <= 45){ setMoveShortcut(RING, MOVES[1]); }
            else { setMoveShortcut(SMACK, MOVES[2]); }
        }
        else if(lastMove(RING)){
            if(num <= 45){ setMoveShortcut(CUCKOO, MOVES[0]); }
            else { setMoveShortcut(SMACK, MOVES[2]); }
        }
        else if(lastMove(SMACK)){
            if(num <= 45){ setMoveShortcut(CUCKOO, MOVES[0]); }
            else { setMoveShortcut(RING, MOVES[1]); }
        }
        else {
            if(num <= 33){ setMoveShortcut(CUCKOO, MOVES[0]); }
            else if (num <= 66){ setMoveShortcut(RING, MOVES[1]); }
            else { setMoveShortcut(SMACK, MOVES[2]); }
        }
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new AbstractLambdaPower(lock_pname, lock_pid, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() { description = lock_desc[0]; }
        });
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        atb(new MakeTempCardInHandAction(new ForestKeeperLock(), 1));
        for (int i = 0; i < bird.minions.length; i++) {
            if (bird.minions[i] == this) {
                bird.minions[i] = null;
                break;
            }
        }
    }
}