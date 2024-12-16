package ruina.monsters.act1.nothingDer;

import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.RuinaMod;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.multiplayer.MultiplayerEnemyBuff;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Gunman extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(Gunman.class.getSimpleName());

    public static final String LASER = RuinaMod.makeMonsterPath("Gunman/Laser.png");
    private static final Texture LASER_TEXTURE = TexLoader.getTexture(LASER);

    private static final byte RUTHLESS_BULLETS = 0;
    private static final byte INEVITABLE_BULLET = 1;
    private static final byte SILENT_SCOPE = 2;
    private static final byte MAGIC_BULLET = 3;
    private static final byte DEATH_MARK = 4;

    private static final int MASS_ATTACK_COOLDOWN = 2;
    private int counter = MASS_ATTACK_COOLDOWN;

    private final int BLOCK = calcAscensionTankiness(10);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int DEBUFF = calcAscensionSpecial(1);
    private final int VULNERABLE = 1;

    public Gunman() {
        this(100.0f, 0.0f);
    }

    public Gunman(final float x, final float y) {
        super(ID, ID, 180, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Gunman/Spriter/Gunman.scml"));
        setNumAdditionalMoves(1);
        this.setHp(calcAscensionTankiness(180));

        addMove(RUTHLESS_BULLETS, IntentEnums.MASS_ATTACK, calcAscensionDamage(17));
        addMove(INEVITABLE_BULLET, Intent.ATTACK, calcAscensionDamage(7));
        addMove(SILENT_SCOPE, Intent.DEFEND_DEBUFF);
        addMove(MAGIC_BULLET, Intent.ATTACK, 15);
        addMove(DEATH_MARK, Intent.DEBUFF);

        this.icon = TexLoader.getTexture(makeUIPath("GunIcon.png"));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof NothingThere) {
                target = (NothingThere)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        if (RuinaMod.isMultiplayerConnected()) {
            applyToTarget(this, this, new MultiplayerEnemyBuff(this));
        }
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case RUTHLESS_BULLETS: {
                massAttackAnimation(target);
                waitAnimation();
                massAttackEffect();
                int[] damageArray = calcMassAttack(info);
                for (int j = 0; j < damageArray.length - 1; j++) {
                    if (RuinaMod.isMultiplayerConnected() && hasPower(MultiplayerEnemyBuff.POWER_ID)) {
                        damageArray[j] *= (1.0f + ((float)getPower(MultiplayerEnemyBuff.POWER_ID).amount / 100));
                    }
                }
                atb(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                resetIdle();
                waitAnimation();
                counter = MASS_ATTACK_COOLDOWN + 1;
                break;
            }
            case INEVITABLE_BULLET: {
                attackAnimation(target);
                dmg(target, info);
                resetIdle(1.0f);
                break;
            }
            case SILENT_SCOPE: {
                blockAnimation();
                block(this, BLOCK);
                applyToTarget(target, this, new WeakPower(target, DEBUFF, true));
                resetIdle(1.0f);
                break;
            }
            case MAGIC_BULLET: {
                attackAnimation(target);
                dmg(target, info);
                resetIdle(1.0f);
                break;
            }
            case DEATH_MARK: {
                specialAnimation();
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                applyToTarget(target, this, new VulnerablePower(target, VULNERABLE, true));
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void massAttackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BulletFinalShot", enemy, this);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BulletShot", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Dodge", null, this);
    }

    private void specialAnimation() {
        animationAction("Block", "BulletFlame", this);
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            atb(new TalkAction(this, DIALOG[1]));
        }
        super.takeTurn();
        counter--;
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (counter <= 0) {
            setMoveShortcut(RUTHLESS_BULLETS);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(INEVITABLE_BULLET)) {
                possibilities.add(INEVITABLE_BULLET);
            }
            if (!this.lastMove(SILENT_SCOPE)) {
                possibilities.add(SILENT_SCOPE);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (counter == 1) {
            setAdditionalMoveShortcut(DEATH_MARK, moveHistory);
        } else {
            setAdditionalMoveShortcut(MAGIC_BULLET, moveHistory);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case SILENT_SCOPE: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, DEBUFF, DetailedIntent.WEAK_TEXTURE);
                detailsList.add(detail2);
                break;
            }
            case DEATH_MARK: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, VULNERABLE, DetailedIntent.VULNERABLE_TEXTURE);
                detailsList.add(detail2);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!target.isDeadOrEscaped() && target instanceof NothingThere) {
            ((NothingThere)target).onGunManDeath();
            AbstractDungeon.onModifyPower();
        }
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            onBossVictoryLogic();
        }
    }

    public void onNothingDeath() {
        atb(new TalkAction(this, DIALOG[2]));
    }

    private void massAttackEffect() {
        float duration = 0.7f;
        AbstractGameEffect effect = new VfxBuilder(LASER_TEXTURE, (float)Settings.WIDTH * 1.5f, this.hb.cY, duration)
                .moveX((float)Settings.WIDTH * 1.5f, -(float)Settings.WIDTH / 2)
                .build();
        atb(new VFXAction(effect, duration));
    }

}