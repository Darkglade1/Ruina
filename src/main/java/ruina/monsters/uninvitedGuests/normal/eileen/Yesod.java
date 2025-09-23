package ruina.monsters.uninvitedGuests.normal.eileen;

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
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;
import com.megacrit.cardcrawl.powers.EnergizedPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.RuinaMod;
import ruina.actions.AllyGainBlockAction;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.uninvitedGuests.normal.eileen.yesodCards.FloodingBullets;
import ruina.monsters.uninvitedGuests.normal.eileen.yesodCards.Reload;
import ruina.powers.act4.DarkBargain;
import ruina.powers.multiplayer.MultiplayerAllyBuff;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class Yesod extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Yesod.class.getSimpleName());

    private static final byte RELOAD = 0;
    private static final byte FLOODING_BULLETS = 1;

    public static final String LASER = RuinaMod.makeMonsterPath("Yesod/Laser.png");
    private static final Texture LASER_TEXTURE = TexLoader.getTexture(LASER);

    public final int BLOCK = 14;
    public final int ENERGY = 1;
    public final int DRAW = 1;
    public final int bulletHits = 3;
    public final float initialDamageBonus = 2.0f;
    public final float damageGrowth = 0.5f;
    public float currentDamageBonus = initialDamageBonus;
    public float currDamageGrowth = damageGrowth;

    public Yesod() {
        this(0.0f, 0.0f);
    }

    public Yesod(final float x, final float y) {
        super(ID, ID, 160, -5.0F, 0, 230.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Yesod/Spriter/Yesod.scml"));
        this.animation.setFlip(true, false);
        massAttackHitsPlayer = true;
        this.setHp(calcAscensionTankiness(160));

        addMove(RELOAD, Intent.DEFEND_BUFF);
        addMove(FLOODING_BULLETS, IntentEnums.MASS_ATTACK, calcAscensionDamage(8), bulletHits);

        cardList.add(new Reload(this));
        cardList.add(new FloodingBullets(this));

        this.icon = TexLoader.getTexture(makeUIPath("YesodIcon.png"));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Eileen) {
                target = (Eileen)mo;
            }
        }
//        if (RuinaMod.isMultiplayerConnected()) {
//            currDamageGrowth = damageGrowth / P2PManager.GetPlayerCount();
//        }
        addPower(new DarkBargain(this));
        super.usePreBattleAction();

        if (RuinaMod.isMultiplayerConnected()) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractPower darkBargain = getPower(DarkBargain.POWER_ID);
                    if (darkBargain != null) {
                        darkBargain.updateDescription();
                    }
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public void takeTurn() {
        if (firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();
        switch (this.nextMove) {
            case RELOAD: {
                specialAnimation();
                atb(new AllyGainBlockAction(this, this, BLOCK));
                applyToTarget(adp(), this, new EnergizedPower(adp(), ENERGY));
                applyToTarget(adp(), this, new DrawCardNextTurnPower(adp(), DRAW));
                resetIdle(1.0f);
                break;
            }
            case FLOODING_BULLETS: {
                int[] damageArray = calcMassAttack(info);
                for (int i = 0; i < damageArray.length - 1; i++) {
                    damageArray[i] *= currentDamageBonus;
                    if (RuinaMod.isMultiplayerConnected() && hasPower(MultiplayerAllyBuff.POWER_ID)) {
                        damageArray[i] *= (1.0f + ((float)getPower(MultiplayerAllyBuff.POWER_ID).amount / 100));
                    }
                }
                for (int i = 0; i < multiplier; i++) {
                    rangeAnimation(target);
                    waitAnimation();
                    massAttackEffect();
                    atb(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                    resetIdle();
                    waitAnimation();
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(FLOODING_BULLETS)) {
            setMoveShortcut(RELOAD);
        } else {
            setMoveShortcut(FLOODING_BULLETS);
        }
    }

    public void onBossDeath() {
        this.halfDead = true; //stop aoe from doing damage
        if (!isDead && !isDying) {
            atb(new TalkAction(this, DIALOG[1]));
            atb(new VFXAction(new WaitEffect(), 1.0F));
            //double addToBot in case boxx dies to combust-esque effect LMAO
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            disappear();
                            this.isDone = true;
                        }
                    });
                    this.isDone = true;
                }
            });
        }
    }

    private void specialAnimation() {
        animationAction("Special", "BulletFlame", this);
    }

    private void rangeAnimation(AbstractCreature enemy) {
        animationAction("Ranged", "BulletFinalShot", enemy, this);
    }

    private void massAttackEffect() {
        float duration = 0.7f;
        AbstractGameEffect effect = new VfxBuilder(LASER_TEXTURE, -(float)Settings.WIDTH / 2, this.hb.cY, duration)
                .moveX(-(float)Settings.WIDTH / 2, (float)Settings.WIDTH * 1.5f)
                .build();
        atb(new VFXAction(effect, duration));
    }

}