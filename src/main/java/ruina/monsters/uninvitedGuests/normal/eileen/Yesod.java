package ruina.monsters.uninvitedGuests.normal.eileen;

import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
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
import ruina.powers.AbstractLambdaPower;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Yesod extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Yesod.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte RELOAD = 0;
    private static final byte FLOODING_BULLETS = 1;

    public static final String LASER = RuinaMod.makeMonsterPath("Yesod/Laser.png");
    private static final Texture LASER_TEXTURE = TexLoader.getTexture(LASER);

    public final int BLOCK = 14;
    public final int ENERGY = 1;
    public final int DRAW = 1;
    public final int bulletHits = 3;
    public final float damageBonus = 2.0f;
    public final float damageGrowth = 0.5f;
    public float currentDamageBonus = damageBonus;

    public Eileen eileen;

    public static final String POWER_ID = makeID("DarkBargain");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final Texture targetTexture = TexLoader.getTexture(makeUIPath("YesodIcon.png"));

    public Yesod() {
        this(0.0f, 0.0f);
    }

    public Yesod(final float x, final float y) {
        super(NAME, ID, 160, -5.0F, 0, 230.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Yesod/Spriter/Yesod.scml"));
        this.animation.setFlip(true, false);
        massAttackHitsPlayer = true;
        this.type = EnemyType.BOSS;
        this.setHp(calcAscensionTankiness(160));

        addMove(RELOAD, Intent.DEFEND_BUFF);
        addMove(FLOODING_BULLETS, IntentEnums.MASS_ATTACK, calcAscensionDamage(8), bulletHits, true);

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
                eileen = (Eileen)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (target == adp() && damageAmount <= 0 && info.type == DamageInfo.DamageType.NORMAL) {
                    flash();
                    currentDamageBonus += damageGrowth;
                    updateDescription();
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + currentDamageBonus + POWER_DESCRIPTIONS[1] + damageGrowth + POWER_DESCRIPTIONS[2];
            }
        });
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        if (this.isDead) {
            return;
        }
        super.takeTurn();
        if (firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
            firstMove = false;
        }

        DamageInfo info;
        int multiplier = 0;
        if(moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }

        AbstractCreature target = eileen;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
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
                int[] damageArray = new int[AbstractDungeon.getMonsters().monsters.size() + 1];
                info.applyPowers(this, adp());
                damageArray[damageArray.length - 1] = info.output;
                for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                    info.applyPowers(this, mo);
                    info.output *= currentDamageBonus;
                    damageArray[i] = info.output;
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
            setMoveShortcut(RELOAD, MOVES[RELOAD], cardList.get(RELOAD));
        } else {
            setMoveShortcut(FLOODING_BULLETS, MOVES[FLOODING_BULLETS], cardList.get(FLOODING_BULLETS));
        }
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1) {
            super.applyPowers();
            return;
        }
        applyPowers(eileen);
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