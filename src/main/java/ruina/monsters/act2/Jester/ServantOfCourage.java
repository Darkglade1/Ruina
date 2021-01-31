package ruina.monsters.act2.Jester;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.powers.Erosion;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.dmg;

public class ServantOfCourage extends AbstractMagicalGirl
{
    public static final String ID = RuinaMod.makeID(ServantOfCourage.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte HELP = 0;
    private static final byte PROTECT_FRIEND = 1;

    private static final int DEBUFF_AMT = 3;

    public ServantOfCourage() {
        this(0.0f, 0.0f, null);
    }

    public ServantOfCourage(final float x, final float y, JesterOfNihil jester) {
        super(NAME, ID, 120, -5.0F, 0, 170.0f, 235.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ServantOfCourage/Spriter/ServantOfCourage.scml"));
        this.animation.setFlip(true, false);
        this.type = EnemyType.BOSS;

        this.setHp(160);

        addMove(HELP, Intent.ATTACK, 7, 2, true);
        addMove(PROTECT_FRIEND, Intent.STRONG_DEBUFF);

        this.jester = jester;
        this.allyIcon = makeUIPath("CourageIcon.png");
    }

    @Override
    public void takeTurn() {
        if (this.isDead) {
            return;
        }
        super.takeTurn();

        DamageInfo info;
        int multiplier = 0;
        if(moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }

        AbstractCreature target = jester;
        if(info.base > -1) {
            info.applyPowers(this, target);
        }

        switch (this.nextMove) {
            case HELP: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        attack1Animation(target);
                    } else {
                        attack2Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case PROTECT_FRIEND: {
                debuffAnimation(target);
                applyToTarget(target, this, new Erosion(target, DEBUFF_AMT));
                resetIdle(0.5f);
                break;
            }
        }
    }

    @Override
    protected void getMove(final int num) {
        if (this.currentHealth <= this.maxHealth * 0.30f) {
            setMoveShortcut(HELP, MOVES[HELP]);
        } else {
            if (this.lastMove(PROTECT_FRIEND)) {
                setMoveShortcut(HELP, MOVES[HELP]);
            } else {
                setMoveShortcut(PROTECT_FRIEND, MOVES[PROTECT_FRIEND]);
            }
        }
    }

    private void attack1Animation(AbstractCreature enemy) {
        animationAction("Attack1", "WoodStrike", enemy, this);
    }

    private void attack2Animation(AbstractCreature enemy) {
        animationAction("Attack2", "WoodFinish", enemy, this);
    }

    private void debuffAnimation(AbstractCreature enemy) {
        animationAction("Attack3", "GreedGetPower", enemy, this);
    }

    public String getSummonDialog() {
        return DIALOG[0];
    }

    public String getVictoryDialog() {
        return DIALOG[1];
    }

    public String getDeathDialog() {
        return DIALOG[2];
    }

}