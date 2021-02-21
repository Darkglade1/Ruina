package ruina.monsters.act3;

import basemod.animations.AbstractAnimation;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.monsters.act2.LittleRed;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Bleed;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Twilight extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(Twilight.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte CRUEL_CLAWS = 0;
    private static final byte FEROCIOUS_FANGS = 1;
    private static final byte BLOODSTAINED_HUNT = 2;
    private static final byte HOWL = 3;

    private final int BLOCK = calcAscensionTankiness(20);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int HEAL = calcAscensionSpecial(100);
    private final int BLEED = calcAscensionSpecial(2);

    public static final String POWER_ID = makeID("BloodstainedClaws");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final AbstractAnimation bird;

    public Twilight() {
        this(0.0f, 0.0f);
    }

    public Twilight(final float x, final float y) {
        super(NAME, ID, 600, -5.0F, 0, 330.0f, 305.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Twilight/Spriter/Twilight.scml"));
        this.bird = new BetterSpriterAnimation(makeMonsterPath("Twilight/Bird/Bird.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(this.maxHealth));

        addMove(CRUEL_CLAWS, Intent.ATTACK_DEFEND, calcAscensionDamage(9));
        addMove(FEROCIOUS_FANGS, Intent.ATTACK_DEBUFF, calcAscensionDamage(7), 2, true);
        addMove(BLOODSTAINED_HUNT, Intent.ATTACK, calcAscensionDamage(6), 3, true);
        addMove(HOWL, Intent.BUFF);
    }

    @Override
    public void usePreBattleAction() {
//        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, HEAL) {
//            @Override
//            public void updateDescription() {
//                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
//            }
//        });
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case CRUEL_CLAWS: {
                block(this, BLOCK);
                dmg(target, info);
                resetIdle();
                break;
            }
            case FEROCIOUS_FANGS: {
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info);
                    resetIdle();
                }
                if (target == adp()) {
                    applyToTarget(target, this, new Bleed(target, BLEED));
                } else {
                    applyToTarget(target, this, new Bleed(target, BLEED, true));
                }
                break;
            }
            case BLOODSTAINED_HUNT: {
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case HOWL: {
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
        }
    }

//    private void clawAnimation(AbstractCreature enemy) {
//        animationAction("Claw", "Claw", enemy, this);
//    }
//
//    private void biteAnimation(AbstractCreature enemy) {
//        animationAction("Bite", "Bite", enemy, this);
//    }
//
//    private void howlAnimation() {
//        animationAction("Howl", "Howl", this);
//    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            takeCustomTurn(additionalMove, adp());
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(CRUEL_CLAWS)) {
            setMoveShortcut(BLOODSTAINED_HUNT, MOVES[BLOODSTAINED_HUNT]);
        } else if (this.lastMove(FEROCIOUS_FANGS)) {
            setMoveShortcut(CRUEL_CLAWS, MOVES[CRUEL_CLAWS]);
        } else {
            setMoveShortcut(FEROCIOUS_FANGS, MOVES[FEROCIOUS_FANGS]);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (lastMove(BLOODSTAINED_HUNT, moveHistory)) {
            setAdditionalMoveShortcut(HOWL, moveHistory);
        } else if (lastMove(HOWL, moveHistory)) {
            setAdditionalMoveShortcut(FEROCIOUS_FANGS, moveHistory);
        } else {
            setAdditionalMoveShortcut(BLOODSTAINED_HUNT, moveHistory);
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        for (int i = 0; i < additionalIntents.size(); i++) {
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            EnemyMoveInfo additionalMove = null;
            if (i < additionalMoves.size()) {
                additionalMove = additionalMoves.get(i);
            }
            if (additionalMove != null) {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!isDead) {
            sb.setColor(Color.WHITE);
            bird.renderSprite(sb, (float) Settings.WIDTH / 2, (float) Settings.HEIGHT / 2);
        }
        super.render(sb);
    }

}