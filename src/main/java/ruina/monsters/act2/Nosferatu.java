package ruina.monsters.act2;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.VampireDamageActionButItCanFizzle;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Paralysis;
import ruina.vfx.ThirstEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Nosferatu extends AbstractRuinaMonster
{
    public static final String ID = makeID(Nosferatu.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte UNBEARABLE_DROUGHT = 0;
    private static final byte MERCILESS_GESTURE = 1;
    private static final byte LOOMING_PRESENCE = 2;

    private final int STRENGTH = calcAscensionSpecial(3);
    private final int PARALYSIS = calcAscensionSpecial(2);
    private final int VULNERABLE = calcAscensionSpecial(1);

    public Nosferatu() {
        this(0.0f, 0.0f);
    }

    public Nosferatu(final float x, final float y) {
        super(NAME, ID, 40, -5.0F, 0, 250.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Nosferatu/Spriter/Nosferatu.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(90), calcAscensionTankiness(95));
        addMove(UNBEARABLE_DROUGHT, Intent.ATTACK_BUFF, calcAscensionDamage(15));
        addMove(MERCILESS_GESTURE, Intent.ATTACK_DEBUFF, calcAscensionDamage(11));
        addMove(LOOMING_PRESENCE, Intent.DEBUFF);
    }

    @Override
    public void usePreBattleAction() {
        playSound("NosChange");
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
            case UNBEARABLE_DROUGHT: {
                final AbstractGameEffect[] vfx = {null};
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(vfx[0] == null){
                            vfx[0] = new ThirstEffect();
                            AbstractDungeon.effectsQueue.add(vfx[0]);
                            CardCrawlGame.sound.play("ruina:NosSpecial", 0f);
                        }
                        else {
                            isDone = vfx[0].isDone;
                        }
                    }
                });
                attack1Animation(adp());
                atb(new VampireDamageActionButItCanFizzle(adp(), info, AbstractGameAction.AttackEffect.NONE));
                resetIdle();
                break;
            }
            case MERCILESS_GESTURE: {
                attack2Animation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new VulnerablePower(adp(), VULNERABLE, true));
                resetIdle();
                break;
            }
            case LOOMING_PRESENCE: {
                specialAnimation();
                applyToTarget(adp(), this, new Paralysis(adp(), PARALYSIS));
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(MERCILESS_GESTURE)) {
            setMoveShortcut(UNBEARABLE_DROUGHT, MOVES[UNBEARABLE_DROUGHT]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(UNBEARABLE_DROUGHT)) {
                possibilities.add(UNBEARABLE_DROUGHT);
            }
            if (!this.lastMove(MERCILESS_GESTURE)) {
                possibilities.add(MERCILESS_GESTURE);
            }
            if (!this.lastMove(LOOMING_PRESENCE) && !this.lastMoveBefore(LOOMING_PRESENCE)) {
                possibilities.add(LOOMING_PRESENCE);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    private void attack1Animation(AbstractCreature enemy) {
        animationAction("Attack1", "NosBloodEat", enemy, this);
    }

    private void attack2Animation(AbstractCreature enemy) {
        animationAction("Attack2", "NosGrab", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "NosSpecial", this);
    }

}