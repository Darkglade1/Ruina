package ruina.monsters.act3;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Paralysis;
import ruina.powers.act3.Sin;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class JudgementBird extends AbstractRuinaMonster
{
    public static final String ID = makeID(JudgementBird.class.getSimpleName());

    private static final byte STARE = 0;
    private static final byte JUDGEMENT = 1;
    private static final byte HEAVY_GUILT = 2;

    private final int DEBUFF = 1;
    private final int STRENGTH = calcAscensionSpecial(3);
    private final int PARALYSIS = calcAscensionSpecial(2);
    private final int COST_THRESHOLD = calcAscensionSpecial(1);
    private final int COST_INCREASE = 1;

    public JudgementBird() {
        this(0.0f, 0.0f);
    }

    public JudgementBird(final float x, final float y) {
        super(ID, ID, 280, 0.0F, 0, 280.0f, 360.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("JudgementBird/Spriter/JudgementBird.scml"));
        setHp(calcAscensionTankiness(280));
        addMove(STARE, Intent.STRONG_DEBUFF);
        addMove(JUDGEMENT, Intent.ATTACK, calcAscensionDamage(16));
        addMove(HEAVY_GUILT, Intent.ATTACK_DEBUFF, calcAscensionDamage(7));
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new Sin(this, COST_THRESHOLD, COST_INCREASE));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case STARE: {
                specialAnimation(adp());
                applyToTarget(adp(), this, new VulnerablePower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle();
                break;
            }
            case JUDGEMENT: {
                judgementAnimation(adp());
                atb(new VFXAction(new WaitEffect(), 0.25f));
                judgementVfx();
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case HEAVY_GUILT: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Paralysis(adp(), PARALYSIS));
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(HEAVY_GUILT)) {
            setMoveShortcut(STARE);
        } else if (lastMove(STARE)) {
            setMoveShortcut(JUDGEMENT);
        } else {
            setMoveShortcut(HEAVY_GUILT);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case HEAVY_GUILT: {
                DetailedIntent detail = new DetailedIntent(this, PARALYSIS, DetailedIntent.PARALYSIS_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case STARE: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, DEBUFF, DetailedIntent.VULNERABLE_TEXTURE);
                detailsList.add(detail2);
                DetailedIntent detail3 = new DetailedIntent(this, DEBUFF, DetailedIntent.FRAIL_TEXTURE);
                detailsList.add(detail3);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "JudgementAttack", enemy, this);
    }

    private void judgementAnimation(AbstractCreature enemy) {
        animationAction("Judgement", "JudgementDing", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", "JudgementGong", enemy, this);
    }

    public static void judgementVfx() {
        ArrayList<Texture> frames = new ArrayList<>();
        for (int i = 1; i <= 14; i++) {
            frames.add(TexLoader.getTexture(makeMonsterPath("JudgementBird/Hang/Hang" + i + ".png")));
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound("JudgementHang");
                this.isDone = true;
            }
        });
        fullScreenAnimation(frames, 0.1f, 1.4f);
    }

}