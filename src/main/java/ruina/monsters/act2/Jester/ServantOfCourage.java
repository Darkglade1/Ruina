package ruina.monsters.act2.Jester;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Erosion;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

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

    public ServantOfCourage(final float x, final float y) {
        super(NAME, ID, 120, -5.0F, 0, 170.0f, 235.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ServantOfCourage/Spriter/ServantOfCourage.scml"));
        this.animation.setFlip(true, false);
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(120);
        } else {
            this.setHp(140);
        }

        addMove(HELP, Intent.ATTACK, 7, 2, true);
        addMove(PROTECT_FRIEND, Intent.STRONG_DEBUFF);

        this.allyIcon = makeUIPath("CourageIcon.png");
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof JesterOfNihil) {
                jester = (JesterOfNihil) mo;
            }
        }
        super.usePreBattleAction();
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
        atb(new RollMoveAction(this));
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