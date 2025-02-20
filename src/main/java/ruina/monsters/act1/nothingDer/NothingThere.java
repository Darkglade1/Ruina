package ruina.monsters.act1.nothingDer;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.act1.Mimicry;
import ruina.powers.multiplayer.MimicryMultiplayer;
import ruina.powers.multiplayer.MultiplayerEnemyBuff;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class NothingThere extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(NothingThere.class.getSimpleName());
    private static final byte DENSE_FLESH = 0;
    private static final byte EYE_CONTACT = 1;
    private static final byte REACHING_HAND = 2;
    private static final byte EVOLVE = 3;

    private final int BLOCK = calcAscensionTankiness(12);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int STATUS = calcAscensionSpecial(1);

    public NothingThere() {
        this(100.0f, 0.0f);
    }

    public NothingThere(final float x, final float y) {
        super(ID, ID, 220, -5.0F, 0, 250.0f, 205.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("NothingThere/Spriter/NothingThere.scml"));
        this.animation.setFlip(true, false);
        this.flipHorizontal = true; //additionalIntent class checks for this boolean to know when to flip additional intent positioning
        setNumAdditionalMoves(1);
        this.setHp(calcAscensionTankiness(220));

        int attackDamage = 2;
        if (AbstractDungeon.ascensionLevel >= 4) {
            attackDamage = 3;
        }
        addMove(DENSE_FLESH, Intent.DEFEND);
        addMove(EYE_CONTACT, Intent.ATTACK_DEBUFF, attackDamage);
        addMove(REACHING_HAND, Intent.ATTACK, 5, 2);
        addMove(EVOLVE, Intent.BUFF);

        this.icon = TexLoader.getTexture(makeUIPath("NothingIcon.png"));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning3");
        adp().movePosition((float)Settings.WIDTH / 2.0F, AbstractDungeon.floorY);
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Gunman) {
                target = (Gunman)mo;
            }
        }
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        if (RuinaMod.isMultiplayerConnected()) {
            applyToTarget(this, this, new MimicryMultiplayer(this, 0));
            applyToTarget(this, this, new MultiplayerEnemyBuff(this));
        } else {
            applyToTarget(this, this, new Mimicry(this, 0));
        }
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case DENSE_FLESH: {
                blockAnimation();
                block(this, BLOCK);
                resetIdle();
                break;
            }
            case EYE_CONTACT: {
                pierceAnimation(target);
                dmg(target, info);
                intoDiscardMo(new Dazed(), STATUS, this);
                resetIdle();
                break;
            }
            case REACHING_HAND: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case EVOLVE: {
                specialAnimation();
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "NothingNormal", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "NothingStrong", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Special", null, this);
    }

    private void specialAnimation() {
        animationAction("Special", "NothingChange", this);
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractRuinaMonster.playSound("NothingHello", 1.0f);
                    isDone = true;
                }
            });
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(DENSE_FLESH)) {
            setMoveShortcut(EYE_CONTACT);
        } else {
            setMoveShortcut(DENSE_FLESH);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (lastMove(REACHING_HAND, moveHistory)) {
            setAdditionalMoveShortcut(EVOLVE, moveHistory);
        } else {
            setAdditionalMoveShortcut(REACHING_HAND, moveHistory);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case DENSE_FLESH: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case EYE_CONTACT: {
                DetailedIntent detail = new DetailedIntent(this, STATUS, DetailedIntent.DAZED_TEXTURE, DetailedIntent.TargetType.DISCARD_PILE);
                detailsList.add(detail);
                break;
            }
            case EVOLVE: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    public void onGunManDeath() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractRuinaMonster.playSound("Goodbye");
                isDone = true;
            }
        });
        atb(new TalkAction(this, DIALOG[1]));
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!target.isDeadOrEscaped() && target instanceof Gunman) {
            ((Gunman) target).onNothingDeath();
            AbstractDungeon.onModifyPower();
        }
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            onBossVictoryLogic();
        }
    }

}