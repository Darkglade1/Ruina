package ruina.monsters.eventboss.lulu.monster;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.eventboss.lulu.cards.CHRBOSS_FlamingBat;
import ruina.monsters.eventboss.lulu.cards.CHRBOSS_PreparedMind;
import ruina.monsters.eventboss.lulu.cards.CHRBOSS_SetAblaze;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Burn;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Lulu extends AbstractCardMonster {
    public static final String ID = RuinaMod.makeID(Lulu.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte PREPARED_MIND = 0;
    private static final byte FLAMING_BAT = 1;
    private static final byte SET_ABLAZE = 2;

    public final int PREPARED_MIND_STRENGTH = calcAscensionSpecial(2);
    public final int FLAMING_BAT_DAMAGE = calcAscensionDamage(7);
    public final int FLAMING_BAT_VULNERABLE = calcAscensionSpecial(1);
    public final int SET_ABLAZE_DAMAGE = calcAscensionDamage(6);
    public final int SET_ABLAZE_HITS = 2;
    private final int FLAMING_BAT_BURN_AMOUNT = calcAscensionSpecial(2);

    public static final String POWER_ID = makeID("FlamingBat");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Lulu() {
        this(0.0f, 0.0f);
    }

    public Lulu(final float x, final float y) {
        super(NAME, ID, 105, 0.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Lulu/Spriter/Lulu.scml"));

        this.setHp(calcAscensionTankiness(105));
        this.type = EnemyType.ELITE;

        addMove(PREPARED_MIND, Intent.BUFF);
        addMove(FLAMING_BAT, Intent.ATTACK_DEBUFF, FLAMING_BAT_DAMAGE);
        addMove(SET_ABLAZE, Intent.ATTACK, SET_ABLAZE_DAMAGE, SET_ABLAZE_HITS, true);

        cardList.add(new CHRBOSS_PreparedMind(this));
        cardList.add(new CHRBOSS_FlamingBat(this));
        cardList.add(new CHRBOSS_SetAblaze(this));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, FLAMING_BAT_BURN_AMOUNT) {
            @Override
            public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
                    applyToTarget(target, owner, new Burn(target, amount));
                }
            }

            @Override
            public void updateDescription() {
                description = String.format(POWER_DESCRIPTIONS[0], amount);
            }
        });
    }

    @Override
    public void takeTurn() {
        DamageInfo info;
        int multiplier = 0;
        if (moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }
        if (info.base > -1) {
            info.applyPowers(this, adp());
        }
        switch (nextMove) {
            case PREPARED_MIND: {
                guardAnimation();
                applyToTarget(this, this, new StrengthPower(this, PREPARED_MIND_STRENGTH));
                resetIdle();
                break;
            }
            case FLAMING_BAT: {
                pierceAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new VulnerablePower(adp(), FLAMING_BAT_VULNERABLE, true));
                resetIdle();
                break;
            }
            case SET_ABLAZE: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(adp());
                    } else {
                        bluntAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(FLAMING_BAT)) {
            setMoveShortcut(SET_ABLAZE, MOVES[SET_ABLAZE], cardList.get(SET_ABLAZE).makeStatEquivalentCopy());
        } else if (this.lastMove(PREPARED_MIND)) {
            setMoveShortcut(FLAMING_BAT, MOVES[FLAMING_BAT], cardList.get(FLAMING_BAT).makeStatEquivalentCopy());
        } else {
            setMoveShortcut(PREPARED_MIND, MOVES[PREPARED_MIND], cardList.get(PREPARED_MIND).makeStatEquivalentCopy());
        }
    }

    public void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "FireStab", enemy, this);
    }

    public void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "FireHori", enemy, this);
    }

    public void guardAnimation() {
        animationAction("Block", "FireGuard", this);
    }

}