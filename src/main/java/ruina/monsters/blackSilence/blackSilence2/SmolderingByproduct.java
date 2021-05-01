package ruina.monsters.blackSilence.blackSilence2;

import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SmolderingByproduct extends AbstractRuinaMonster
{
    public static final String ID = makeID(SmolderingByproduct.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte STABBING_TENDRILS = 0;
    private static final byte GNAW = 1;

    private final SmolderingAbomination parent;
    private int STABBING_DAMAGE = calcAscensionDamage(6);
    private int STABBING_WEAK = calcAscensionSpecial(1);

    private int GNAW_DAMAGE = calcAscensionDamage(7);
    private int GNAW_HITS = 2;

    public SmolderingByproduct() {
        this(0.0f, 0.0f, null);
    }

    public SmolderingByproduct(final float x, final float y, SmolderingAbomination boss) {
        super(NAME, ID, 50, -5.0F, 0, 100.0f, 205.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("GearsWorshipper/Spriter/GearsWorshipper.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(30));
        addMove(STABBING_TENDRILS, Intent.ATTACK_DEBUFF, STABBING_DAMAGE);
        addMove(GNAW, Intent.ATTACK, GNAW_DAMAGE, GNAW_HITS, true);

        parent = boss;
    }


    @Override
    public void takeTurn() {
        atb(new RemoveAllBlockAction(this, this));
        if (this.firstMove) { firstMove = false; }
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;
        AbstractCreature target;
        target = adp();
        if(info.base > -1) { info.applyPowers(this, target); }
        switch (this.nextMove) {
            case STABBING_TENDRILS: {
                dmg(target, info);
                applyToTarget(adp(), this, new WeakPower(adp(), STABBING_WEAK, true));
                break;
            }
            case GNAW: {
                for (int i = 0; i < multiplier; i++) { dmg(target, info); }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if(firstMove || !lastMove(STABBING_TENDRILS)){ setMoveShortcut(STABBING_TENDRILS, MOVES[STABBING_TENDRILS]); }
        else { setMoveShortcut(GNAW, MOVES[GNAW]); }    }

    @Override
    public void createIntent() {
        super.createIntent();
        applyPowers();
    }


    @Override
    public void damage(DamageInfo info) {
        int previousHealth = currentHealth;
        super.damage(info);
        int nextHealth = currentHealth;
        int difference = previousHealth - nextHealth;
        if (difference > 0) {
            if (parent != null) { atb(new LoseHPAction(parent, parent, difference)); }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (int i = 0; i < parent.minions.length; i++) {
            if (parent.minions[i] == this) {
                parent.minions[i] = null;
                break;
            }
        }
    }

}