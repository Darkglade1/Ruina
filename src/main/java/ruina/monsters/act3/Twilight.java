package ruina.monsters.act3;

import actlikeit.dungeons.CustomDungeon;
import basemod.animations.AbstractAnimation;
import basemod.helpers.CardPowerTip;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.cards.Dazzled;
import ruina.monsters.AbstractRuinaMonster;
import ruina.multiplayer.NetworkTwilight;
import ruina.powers.act3.BigEgg;
import ruina.powers.act3.FadingTwilight;
import ruina.powers.act3.LongEgg;
import ruina.powers.act3.SmallEgg;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;
import spireTogether.networkcore.P2P.P2PManager;
import spireTogether.networkcore.objects.entities.NetworkMonster;
import spireTogether.other.RoomDataManager;
import spireTogether.util.SpireHelp;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Twilight extends AbstractRuinaMonster
{
    public static final String ID = makeID(Twilight.class.getSimpleName());

    private static final Texture SHOCKWAVE = TexLoader.getTexture(makeMonsterPath("Twilight/Shockwave.png"));

    private static final byte PEACE_FOR_ALL = 0;
    private static final byte SURVEILLANCE = 1;
    private static final byte TORN_MOUTH = 2;
    private static final byte TILTED_SCALE = 3;
    private static final byte TALONS = 4;

    private final int BLOCK = calcAscensionTankiness(24);
    private final int VULNERABLE = calcAscensionSpecial(1);
    private final int FRAIL = calcAscensionSpecial(2);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int NUM_DAZZLE = calcAscensionSpecial(1);
    private final int SMALL_EGG_NUM_CARDS = 2;
    private final int SMALL_EGG_COST_INCREASE = calcAscensionSpecial(1);
    private final int EGG_CYCLE_TURN_NUM = 2;

    private enum BirdEgg {
        BIG_EGG, SMALL_EGG, LONG_EGG;
    }

    private final AbstractAnimation bird;
    private final AbstractAnimation bigEgg;
    private final AbstractAnimation smallEgg;
    private final AbstractAnimation longEgg;
    private BirdEgg currentEgg = BirdEgg.BIG_EGG;
    String currentEggPowerID;
    private final AbstractCard status = new Dazzled();

    private static final float HP_THRESHOLD_PERCENT = 0.25f;
    private final int dmgThreshold;
    public int dmgTaken = 0;
    public boolean bigEggBroken = false;
    public boolean smallEggBroken = false;
    public boolean longEggBroken = false;

    public static final int BIG_BIRD_PHASE = 1;
    public static final int SMALL_BIRD_PHASE = 2;
    public static final int LONG_BIRD_PHASE = 3;

    public Twilight() {
        this(0.0f, 0.0f);
    }

    public Twilight(final float x, final float y) {
        super(ID, ID, 550, -5.0F, 0, 330.0f, 305.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Twilight/Spriter/Twilight.scml"));
        this.bird = new BetterSpriterAnimation(makeMonsterPath("Twilight/Bird/Bird.scml"));

        this.bigEgg = new BetterSpriterAnimation(makeMonsterPath("Twilight/Eggs/Eggs.scml"));
        ((BetterSpriterAnimation)bigEgg).myPlayer.setAnimation("BigEgg");
        this.smallEgg = new BetterSpriterAnimation(makeMonsterPath("Twilight/Eggs/Eggs.scml"));
        ((BetterSpriterAnimation)smallEgg).myPlayer.setAnimation("SmallEgg");
        this.longEgg = new BetterSpriterAnimation(makeMonsterPath("Twilight/Eggs/Eggs.scml"));
        ((BetterSpriterAnimation)longEgg).myPlayer.setAnimation("LongEgg");

        this.setHp(calcAscensionTankiness(550));
        dmgThreshold = (int)(this.maxHealth * HP_THRESHOLD_PERCENT);

        addMove(PEACE_FOR_ALL, Intent.ATTACK, calcAscensionDamage(36));
        addMove(SURVEILLANCE, Intent.ATTACK_DEBUFF, calcAscensionDamage(16));
        addMove(TORN_MOUTH, Intent.ATTACK_BUFF, calcAscensionDamage(14));
        addMove(TILTED_SCALE, Intent.DEFEND_DEBUFF);
        addMove(TALONS, Intent.ATTACK, calcAscensionDamage(12), 2);

        Player.PlayerListener listener = new BirdListener(this);
        ((BetterSpriterAnimation)this.bird).myPlayer.addListener(listener);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Roland3");
        playSound("BossBirdBirth", 0.5f);
        switchEgg(currentEgg);
        applyToTarget(this, this, new FadingTwilight(this, EGG_CYCLE_TURN_NUM, dmgThreshold));

        if (RuinaMod.isMultiplayerConnected()) {
            AbstractPower power = this.getPower(FadingTwilight.POWER_ID);
            if (power != null) {
                if (power instanceof TwoAmountPower) {
                    ((TwoAmountPower) power).amount2 = dmgThreshold - dmgTaken;
                    power.updateDescription();
                }
            }
            if (bigEggBroken) {
                ((BetterSpriterAnimation)bigEgg).myPlayer.setAnimation("BigEggBroken");
            }
            if (smallEggBroken) {
                ((BetterSpriterAnimation)smallEgg).myPlayer.setAnimation("SmallEggBroken");
            }
            if (longEggBroken) {
                ((BetterSpriterAnimation)longEgg).myPlayer.setAnimation("LongEggBroken");
            }
            if (hasPower(BigEgg.POWER_ID)) {
                currentEgg = BirdEgg.BIG_EGG;
                currentEggPowerID = BigEgg.POWER_ID;
            }
            if (hasPower(SmallEgg.POWER_ID)) {
                currentEgg = BirdEgg.SMALL_EGG;
                currentEggPowerID = SmallEgg.POWER_ID;
            }
            if (hasPower(LongEgg.POWER_ID)) {
                currentEgg = BirdEgg.LONG_EGG;
                currentEggPowerID = LongEgg.POWER_ID;
            }
        }
    }

    public int getNumEggsLeft() {
        int count = 0;
        if (!bigEggBroken) {
            count++;
        }
        if (!smallEggBroken) {
            count++;
        }
        if (!longEggBroken) {
            count++;
        }
        return count;
    }

    public void cycleEgg() {
        if (currentEgg == BirdEgg.BIG_EGG) {
            if (!smallEggBroken) {
                switchEgg(BirdEgg.SMALL_EGG);
            } else if (!longEggBroken) {
                switchEgg(BirdEgg.LONG_EGG);
            }
        } else if (currentEgg == BirdEgg.SMALL_EGG) {
            if (!longEggBroken) {
                switchEgg(BirdEgg.LONG_EGG);
            } else if (!bigEggBroken) {
                switchEgg(BirdEgg.BIG_EGG);
            }
        } else if (currentEgg == BirdEgg.LONG_EGG) {
            if (!bigEggBroken) {
                switchEgg(BirdEgg.BIG_EGG);
            } else if (!smallEggBroken) {
                switchEgg(BirdEgg.SMALL_EGG);
            }
        }
    }
    
    private void switchEgg(BirdEgg egg) {
        makePowerRemovable(this, currentEggPowerID);
        atb(new RemoveSpecificPowerAction(this, this, currentEggPowerID));
        AbstractPower eggPower = null;
        switch(egg) {
            case BIG_EGG:
                eggPower = new BigEgg(this, NUM_DAZZLE, status);
                applyToTarget(this, this, eggPower);
                break;
            case SMALL_EGG:
                eggPower = new SmallEgg(this, SMALL_EGG_NUM_CARDS, SMALL_EGG_COST_INCREASE);
                applyToTarget(this, this, eggPower);
                break;
            case LONG_EGG:
                if (AbstractDungeon.ascensionLevel >= 19) {
                    atb(new RemoveDebuffsAction(this));
                }
                eggPower = new LongEgg(this);
                applyToTarget(this, this, eggPower);
                break;
        }
        currentEgg = egg;
        currentEggPowerID = eggPower.ID;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (nextMove) {
            case PEACE_FOR_ALL: {
                commandAnimation();
                smashAnimation(adp(), info);
                resetIdle();
                setPhase(phase + 1);
                break;
            }
            case SURVEILLANCE: {
                lampAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new FrailPower(adp(), FRAIL, true));
                resetIdle();
                break;
            }
            case TORN_MOUTH: {
                punishAnimation(adp());
                dmg(adp(), info);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle();
                break;
            }
            case TILTED_SCALE: {
                specialAnimation();
                block(this, BLOCK);
                applyToTarget(adp(), this, new VulnerablePower(adp(), VULNERABLE, true));
                resetIdle();
                break;
            }
            case TALONS: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        crushAnimation(adp());
                    } else {
                        slamAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                setPhase(phase + 1);
                break;
            }
        }
        if (phase > LONG_BIRD_PHASE) {
            setPhase(BIG_BIRD_PHASE);
        }
        atb(new RollMoveAction(this));
    }

    private void setAttack() {
        if (usedTalonsLast()) {
            setMoveShortcut(PEACE_FOR_ALL);
        } else {
            setMoveShortcut(TALONS);
        }
    }

    @Override
    protected void getMove(final int num) {
        if (phase == BIG_BIRD_PHASE) {
            if (!this.lastMove(SURVEILLANCE)) {
                setMoveShortcut(SURVEILLANCE);
            } else {
                setAttack();
            }
        } else if (phase == SMALL_BIRD_PHASE) {
            if (!this.lastMove(TORN_MOUTH)) {
                setMoveShortcut(TORN_MOUTH);
            } else {
                setAttack();
            }
        } else {
            if (!this.lastMove(TILTED_SCALE)) {
                setMoveShortcut(TILTED_SCALE);
            } else {
                setAttack();
            }
        }
    }

    private boolean usedTalonsLast() {
        for (int i = moveHistory.size() - 1; i >= 0; i--) {
            byte move = moveHistory.get(i);
            if (move == PEACE_FOR_ALL) {
                return false;
            }
            if (move == TALONS) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case SURVEILLANCE: {
                DetailedIntent detail = new DetailedIntent(this, FRAIL, DetailedIntent.FRAIL_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case TORN_MOUTH: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case TILTED_SCALE: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, VULNERABLE, DetailedIntent.VULNERABLE_TEXTURE);
                detailsList.add(detail2);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void damage(DamageInfo info) {
        int tmpHealth = this.currentHealth;
        super.damage(info);
        if (!this.isDeadOrEscaped()) {
            this.dmgTaken += tmpHealth - this.currentHealth;
            if (this.dmgTaken >= this.dmgThreshold) {
                this.dmgTaken = 0;
                if (currentEgg == BirdEgg.BIG_EGG) {
                    ((BetterSpriterAnimation)bigEgg).myPlayer.setAnimation("BigEggBroken");
                    bigEggBroken = true;
                }
                if (currentEgg == BirdEgg.SMALL_EGG) {
                    ((BetterSpriterAnimation)smallEgg).myPlayer.setAnimation("SmallEggBroken");
                    smallEggBroken = true;
                }
                if (currentEgg == BirdEgg.LONG_EGG) {
                    ((BetterSpriterAnimation)longEgg).myPlayer.setAnimation("LongEggBroken");
                    longEggBroken = true;
                }
                if (bigEggBroken && smallEggBroken && longEggBroken) {
                    currentEgg = null;
                    makePowerRemovable(this, currentEggPowerID);
                    makePowerRemovable(this, FadingTwilight.POWER_ID);
                    atb(new RemoveSpecificPowerAction(this, this, currentEggPowerID));
                    atb(new RemoveSpecificPowerAction(this, this, FadingTwilight.POWER_ID));
                } else {
                    cycleEgg();
                }
            }
            AbstractPower power = this.getPower(FadingTwilight.POWER_ID);
            if (power != null) {
                if (power instanceof TwoAmountPower) {
                    ((TwoAmountPower) power).amount2 = dmgThreshold - dmgTaken;
                    power.updateDescription();
                }
            }
            updateTwilightMultiplayer();
        }
    }

    private void updateTwilightMultiplayer() {
        if (RuinaMod.isMultiplayerConnected()) {
            P2PManager.SendData(NetworkTwilight.request_updateTwilight, dmgTaken, bigEggBroken, smallEggBroken, longEggBroken, SpireHelp.Gameplay.CreatureToUID(this), SpireHelp.Gameplay.GetMapLocation());
            NetworkMonster m = RoomDataManager.GetMonsterForCurrentRoom(this);
            if (m instanceof NetworkTwilight) {
                ((NetworkTwilight)m).dmgTaken = this.dmgTaken;
                ((NetworkTwilight)m).bigEggBroken = this.bigEggBroken;
                ((NetworkTwilight)m).smallEggBroken = this.smallEggBroken;
                ((NetworkTwilight)m).longEggBroken = this.longEggBroken;
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        onBossVictoryLogic();
        this.onFinalBossVictoryLogic();
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!isDead) {
            sb.setColor(Color.WHITE);
            bird.renderSprite(sb, (float) Settings.WIDTH / 2, (float) Settings.HEIGHT / 2);
            bigEgg.renderSprite(sb, (float) Settings.WIDTH / 2 - (100.0f * Settings.scale), hb.y);
            smallEgg.renderSprite(sb, (float) Settings.WIDTH / 2, hb.y);
            longEgg.renderSprite(sb, (float) Settings.WIDTH / 2 + (100.0f * Settings.scale), hb.y);

            //renders the orbs here again so the giant bird doesn't cover them up
            if (!adp().orbs.isEmpty()) {
                for (AbstractOrb orb : adp().orbs) {
                    orb.render(sb);
                }
            }
        }
        super.render(sb);
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        if (currentEgg == BirdEgg.BIG_EGG) {
            tips.add(new CardPowerTip(status.makeStatEquivalentCopy()));
        }
    }

    private void crushAnimation(AbstractCreature enemy) {
        animationAction("Crush", "BossBirdCrush", enemy, this);
    }

    private void slamAnimation(AbstractCreature enemy) {
        animationAction("Slam", "BossBirdSlam", enemy, this);
    }

    private void lampAnimation(AbstractCreature enemy) {
        animationAction("Lamp", "BossBirdLamp", enemy, this);
    }

    private void punishAnimation(AbstractCreature enemy) {
        animationAction("Punish", "BossBirdPunish", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "BossBirdSpecial", this);
    }

    private void commandAnimation() {
        animationAction("Command", null, this);
    }

    private void smashAnimation(AbstractCreature target, DamageInfo info) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                ((BetterSpriterAnimation)bird).myPlayer.setAnimation("Smash");
                this.isDone = true;
            }
        });
        atb(new VFXAction(new WaitEffect(), 1.3f));
        dmg(target, info);
        float duration = 0.7f;
        AbstractGameEffect shockwaveEffect = new VfxBuilder(SHOCKWAVE, adp().hb.cX, adp().hb.y, duration)
                .fadeOut(0.5f)
                .scale(0.8f, 2.2f, VfxBuilder.Interpolations.LINEAR)
                .playSoundAt(0.0f, makeID("BossBirdStrong"))
                .build();
        atb(new VFXAction(shockwaveEffect, duration));
    }



    public static class BirdListener implements Player.PlayerListener {

        private final Twilight character;

        public BirdListener(Twilight character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (!animation.name.equals("Idle")) {
                ((BetterSpriterAnimation)character.bird).myPlayer.setAnimation("Idle");
            }
        }

        //UNUSED
        public void animationChanged(Animation var1, Animation var2){

        }

        //UNUSED
        public void preProcess(Player var1){

        }

        //UNUSED
        public void postProcess(Player var1){

        }

        //UNUSED
        public void mainlineKeyChanged(com.brashmonkey.spriter.Mainline.Key var1, com.brashmonkey.spriter.Mainline.Key var2){

        }
    }

}