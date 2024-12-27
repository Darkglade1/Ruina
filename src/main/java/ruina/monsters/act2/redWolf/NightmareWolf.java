package ruina.monsters.act2.redWolf;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.Bleed;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class NightmareWolf extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(NightmareWolf.class.getSimpleName());

    private static final byte CRUEL_CLAWS = 0;
    private static final byte FEROCIOUS_FANGS = 1;
    private static final byte BLOODSTAINED_HUNT = 2;
    private static final byte HOWL = 3;

    private final int BLOCK = calcAscensionTankiness(20);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int BLEED = calcAscensionSpecial(2);

    public NightmareWolf() {
        this(0.0f, 0.0f);
    }

    public NightmareWolf(final float x, final float y) {
        super(ID, ID, 450, -5.0F, 0, 330.0f, 285.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("NightmareWolf/Spriter/NightmareWolf.scml"));
        setNumAdditionalMoves(1);
        this.setHp(calcAscensionTankiness(450));

        addMove(CRUEL_CLAWS, Intent.ATTACK_DEFEND, calcAscensionDamage(9));
        addMove(FEROCIOUS_FANGS, Intent.ATTACK_DEBUFF, calcAscensionDamage(7), 2);
        addMove(BLOODSTAINED_HUNT, Intent.ATTACK, calcAscensionDamage(6), 3);
        addMove(HOWL, Intent.BUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof LittleRed) {
                target = (LittleRed)mo;
            }
        }
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case CRUEL_CLAWS: {
                block(this, BLOCK);
                clawAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case FEROCIOUS_FANGS: {
                for (int i = 0; i < multiplier; i++) {
                    biteAnimation(target);
                    dmg(target, info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
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
                    if (i % 2 == 0) {
                        clawAnimation(target);
                    } else {
                        biteAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case HOWL: {
                howlAnimation();
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void clawAnimation(AbstractCreature enemy) {
        animationAction("Claw", "Claw", enemy, this);
    }

    private void biteAnimation(AbstractCreature enemy) {
        animationAction("Bite", "Bite", enemy, this);
    }

    private void howlAnimation() {
        animationAction("Howl", "Howl", this);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(CRUEL_CLAWS)) {
            setMoveShortcut(BLOODSTAINED_HUNT);
        } else if (this.lastMove(FEROCIOUS_FANGS)) {
            setMoveShortcut(CRUEL_CLAWS);
        } else {
            setMoveShortcut(FEROCIOUS_FANGS);
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
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case CRUEL_CLAWS: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case FEROCIOUS_FANGS: {
                DetailedIntent detail = new DetailedIntent(this, BLEED, DetailedIntent.BLEED_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case HOWL: {
                DetailedIntent detail2 = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail2);
                break;
            }
        }
        return detailsList;
    }

    public void onRedDeath() {
        playSound("Fog", 1.5f);
        AbstractDungeon.scene.nextRoom(AbstractDungeon.getCurrRoom()); //switches bg
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.isDead || this.currentHealth <= 0) {
            if (info.owner == target && info.owner instanceof LittleRed) {
                ((LittleRed) info.owner).onKillWolf();
            } else {
                if (!target.isDead && !target.isDying && target instanceof LittleRed) {
                    LittleRed red = (LittleRed)target;
                    addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            red.enrage(true);
                            this.isDone = true;
                        }
                    });
                } else {
                    onBossVictoryLogic();
                }
            }
        }
    }

}