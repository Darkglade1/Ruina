package ruina.monsters.act2.jester;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.powers.act2.Justice;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class QueenOfLove extends AbstractMagicalGirl
{
    public static final String ID = RuinaMod.makeID(QueenOfLove.class.getSimpleName());

    private static final byte LOVE_AND_JUSTICE = 0;
    private static final byte ARCANA_BEATS = 1;

    private final int STRENGTH = 2;

    public QueenOfLove(final float x, final float y) {
        super(ID, ID, 120, -5.0F, 0, 170.0f, 215.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("QueenOfLove/Spriter/QueenOfLove.scml"));
        this.animation.setFlip(true, false);
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(120);
        } else {
            this.setHp(140);
        }

        addMove(LOVE_AND_JUSTICE, Intent.ATTACK, 3, 3);
        addMove(ARCANA_BEATS, Intent.BUFF);

        this.icon = TexLoader.getTexture(makeUIPath("LoveIcon.png"));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new Justice(this));
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        if (firstMove) {
            atb(new TalkAction(this, this.getSummonDialog()));
        }
        super.takeTurn();
        AbstractMonster mo = this;
        AbstractMonster jester = target;
        switch (this.nextMove) {
            case LOVE_AND_JUSTICE: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(target);
                    addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            if (jester.currentBlock == 0) {
                                info.applyPowers(mo, jester);
                                info.output *= 3;
                            }
                            this.isDone = true;
                        }
                    });
                    dmg(target, info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
            case ARCANA_BEATS: {
                buffAnimation(target);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.currentHealth <= this.maxHealth * 0.30f) {
            setMoveShortcut(LOVE_AND_JUSTICE);
        } else {
            if (this.lastMove(LOVE_AND_JUSTICE)) {
                setMoveShortcut(ARCANA_BEATS);
            } else {
                setMoveShortcut(LOVE_AND_JUSTICE);
            }
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case ARCANA_BEATS: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "MagicAttack", enemy, this);
    }

    private void buffAnimation(AbstractCreature enemy) {
        animationAction("Pose", "MagicKiss", enemy, this);
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