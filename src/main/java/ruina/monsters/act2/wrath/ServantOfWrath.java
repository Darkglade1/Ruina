package ruina.monsters.act2.wrath;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.RuinaMod;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.monsters.AbstractAllyMonster;
import ruina.powers.Erosion;
import ruina.powers.MultiplayerAllyBuff;
import ruina.powers.act2.BlindFury;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;
import ruina.vfx.ErosionSplatter;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class ServantOfWrath extends AbstractAllyMonster
{
    public static final String ID = RuinaMod.makeID(ServantOfWrath.class.getSimpleName());

    private static final byte EMBODIMENTS_OF_EVIL = 0;
    private static final byte RAGE = 1;

    private static final int FURY_THRESHOLD = 20;
    private static final int HIGH_ASC_FURY_THRESHOLD = 15;
    private final int furyThreshold;

    private final int EROSION = 2;
    public boolean enraged = false;

    public Hermit hermit;

    public ServantOfWrath() {
        this(0.0f, 0.0f);
    }

    public ServantOfWrath(final float x, final float y) {
        super(ID, ID, 300, -5.0F, 0, 230.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ServantOfWrath/Spriter/Wrath.scml"));
        this.animation.setFlip(true, false);
        massAttackHitsPlayer = true;
        this.setHp(300);

        if (AbstractDungeon.ascensionLevel >= 18) {
            furyThreshold = HIGH_ASC_FURY_THRESHOLD;
        } else {
            furyThreshold = FURY_THRESHOLD;
        }

        addMove(EMBODIMENTS_OF_EVIL, IntentEnums.MASS_ATTACK, calcAscensionDamage(8), 3, true);
        addMove(RAGE, Intent.ATTACK_DEBUFF, 8, 2, true);

        this.icon = TexLoader.getTexture(makeUIPath("WrathIcon.png"));

    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning2");
        playSound("WrathMeet");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Hermit) {
                hermit = (Hermit)mo;
                target = hermit;
            }
        }
        applyToTarget(this, this, new BlindFury(this, furyThreshold));
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            atb(new ShoutAction(this, DIALOG[0], 2.0F, 3.0F));
        }
        super.takeTurn();
        switch (this.nextMove) {
            case EMBODIMENTS_OF_EVIL: {
                for (int i = 0; i < multiplier; i++) {
                    if (i == 0) {
                        big1Animation(target);
                    } else if (i == 1){
                        big2Animation(target);
                    } else {
                        big3Animation(target);
                    }
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            AbstractDungeon.topLevelEffectsQueue.add(new BorderFlashEffect(Color.GREEN));
                            for (int j = 0; j < 6; j++)  {AbstractDungeon.effectsQueue.add(new ErosionSplatter(0.5F)); }
                            isDone = true;
                        }
                    });
                    int[] damageArray = calcMassAttack(info);
                    for (int j = 0; j < damageArray.length - 1; j++) {
                        if (RuinaMod.isMultiplayerConnected() && hasPower(MultiplayerAllyBuff.POWER_ID)) {
                            damageArray[j] *= (1.0f + ((float)getPower(MultiplayerAllyBuff.POWER_ID).amount / 100));
                        }
                    }
                    atb(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                    resetIdle(1.0f);
                }
                applyToTargetNextTurn(adp(), new Erosion(adp(), EROSION + 1));
                for (AbstractMonster mo : monsterList()) {
                    if (mo != this) {
                        applyToTargetNextTurn(mo, new Erosion(mo, EROSION + 1));
                    }
                }
                enraged = false;
                break;
            }
            case RAGE: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        normal1Animation(target);
                    } else {
                        normal2Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                applyToTarget(target, target, new Erosion(target, EROSION));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (enraged) {
            setMoveShortcut(EMBODIMENTS_OF_EVIL);
        } else {
            setMoveShortcut(RAGE);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case RAGE: {
                DetailedIntent detail = new DetailedIntent(this, EROSION, DetailedIntent.EROSION_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case EMBODIMENTS_OF_EVIL: {
                DetailedIntent detail = new DetailedIntent(this, EROSION + 1, DetailedIntent.EROSION_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void applyPowers() {
        if (hermit != null) {
            if (hermit.staff == null || this.intent == IntentEnums.MASS_ATTACK) {
                target = hermit;
            } else {
                target = hermit.staff;
            }
        }
        super.applyPowers();
    }

    public void onHermitDeath() {
        this.halfDead = true; //stop aoe from doing damage
        atb(new TalkAction(this, DIALOG[1]));
        atb(new VFXAction(new WaitEffect(), 1.0F));
        //double addToBot in case hermit dies to combust-esque effect LMAO
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

    private void normal1Animation(AbstractCreature enemy) {
        animationAction("Normal1", "WrathAtk1", enemy, this);
    }

    private void normal2Animation(AbstractCreature enemy) {
        animationAction("Normal2", "WrathAtk2", enemy, this);
    }

    private void big1Animation(AbstractCreature enemy) {
        animationAction("Big1", "WrathStrong1", enemy, this);
    }

    private void big2Animation(AbstractCreature enemy) {
        animationAction("Big2", "WrathStrong2", enemy, this);
    }

    private void big3Animation(AbstractCreature enemy) {
        animationAction("Big3", "WrathStrong3", enemy, this);
    }

}