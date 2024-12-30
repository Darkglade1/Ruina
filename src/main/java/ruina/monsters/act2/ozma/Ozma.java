package ruina.monsters.act2.ozma;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Paralysis;
import ruina.powers.act2.Oblivion;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Ozma extends AbstractRuinaMonster
{
    public static final String ID = makeID(Ozma.class.getSimpleName());

    private static final byte FADING_MEMORIES = 0;
    private static final byte POWDER_OF_LIFE = 1;
    private static final byte HINDER = 2;
    private static final byte SQUASH = 3;

    private final int STRENGTH = calcAscensionSpecial(3);
    private final int PARALYSIS = calcAscensionSpecial(2);
    private final int BLOCK = calcAscensionTankiness(9);
    private final int DRAW_DEBUFF = 1;

    public Ozma() {
        this(0.0f, 0.0f);
    }

    public Ozma(final float x, final float y) {
        super(ID, ID, 500, -5.0F, 0, 250.0f, 215.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Ozma/Spriter/Ozma.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 0.75f);
        setHp(calcAscensionTankiness(90), calcAscensionTankiness(94));
        addMove(FADING_MEMORIES, Intent.STRONG_DEBUFF);
        addMove(POWDER_OF_LIFE, Intent.DEFEND_BUFF);
        addMove(HINDER, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
        addMove(SQUASH, Intent.ATTACK, calcAscensionDamage(17));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case FADING_MEMORIES: {
                debuffAnimation();
                applyToTarget(adp(), this, new Oblivion(adp(), DRAW_DEBUFF));
                resetIdle(1.0f);
                break;
            }
            case POWDER_OF_LIFE: {
                buffAnimation();
                for (AbstractMonster mo : monsterList()) {
                    block(mo, BLOCK);
                    applyToTarget(mo, this, new StrengthPower(this, STRENGTH));
                }
                resetIdle(1.0f);
                break;
            }
            case HINDER: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Paralysis(adp(), PARALYSIS));
                resetIdle(1.0f);
                break;
            }
            case SQUASH: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(FADING_MEMORIES);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(SQUASH)) {
                possibilities.add(SQUASH);
            }
            if (!this.lastMove(HINDER)) {
                possibilities.add(HINDER);
            }
            if (!this.lastMove(POWDER_OF_LIFE) && !this.lastMoveBefore(POWDER_OF_LIFE)) {
                possibilities.add(POWDER_OF_LIFE);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        String textureString = makePowerPath("Oblivion32.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case FADING_MEMORIES: {
                DetailedIntent detail = new DetailedIntent(this, 1, texture);
                detailsList.add(detail);
                break;
            }
            case POWDER_OF_LIFE: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE, DetailedIntent.TargetType.ALL_ENEMIES);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE, DetailedIntent.TargetType.ALL_ENEMIES);
                detailsList.add(detail2);
                break;
            }
            case HINDER: {
                DetailedIntent detail = new DetailedIntent(this, PARALYSIS, DetailedIntent.PARALYSIS_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Slam", "GreedSlam", enemy, this);
    }

    private void buffAnimation() {
        animationAction("Raise", "OzmaGuard", this);
    }

    private void debuffAnimation() {
        animationAction("Thud", "OzmaFin", this);
    }

}