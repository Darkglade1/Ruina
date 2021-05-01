package ruina.monsters.theHead;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import basemod.animations.AbstractAnimation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.BobEffect;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.actions.HeadDialogueAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.theHead.dialogue.HeadDialogue;
import ruina.monsters.uninvitedGuests.normal.elena.Binah;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.Mystery;
import ruina.util.AdditionalIntent;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Zena extends AbstractCardMonster
{
    public static final String ID = makeID(Zena.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    protected static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("MultiIntentStrings"));
    protected static final String[] TEXT = uiStrings.TEXT;

    private static final byte LINE = 0;
    private static final byte THIN_LINE = 1;
    private static final byte THICK_LINE = 2;
    private static final byte SHOCKWAVE = 3;
    private static final byte NONE = 4;

    private static final int MASS_ATTACK_COOLDOWN = 3;
    private int massAttackCooldown = MASS_ATTACK_COOLDOWN;

    public final int BLOCK = calcAscensionTankiness(45);
    public final int DEBUFF = calcAscensionSpecial(2);
    public final int POWER_DEBUFF = calcAscensionSpecial(2);
    public final int THICK_LINE_DEBUFF = calcAscensionSpecial(2);

    public GeburaHead gebura;
    public Baral baral;

    public enum PHASE{
        PHASE1,
        PHASE2
    }

    public PHASE currentPhase;
    private boolean usedPreBattleAction = false;

    public static final String POWER_ID = makeID("AnArbiter");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final Texture targetTexture = TexLoader.getTexture(makeUIPath("ZenaIcon.png"));

    public Zena() {
        this(0.0f, 0.0f, PHASE.PHASE1);
    }
    public Zena(final float x, final float y) { this(x, y, PHASE.PHASE1); }
    public Zena(final float x, final float y, PHASE phase) {
        super(NAME, ID, 999999, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Zena/Spriter/Zena.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 0;
        maxAdditionalMoves = 1;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        currentPhase = phase;
        this.setHp(currentPhase == PHASE.PHASE1 ? maxHealth : calcAscensionTankiness(3000));
        addMove(LINE, Intent.ATTACK, calcAscensionDamage(28));
        addMove(THIN_LINE, Intent.ATTACK_DEBUFF, calcAscensionDamage(24));
        addMove(THICK_LINE, Intent.ATTACK_DEBUFF, calcAscensionDamage(20));
        addMove(SHOCKWAVE, IntentEnums.MASS_ATTACK, calcAscensionDamage(40));
        addMove(NONE, Intent.NONE);
        halfDead = currentPhase.equals(PHASE.PHASE1);
//        cardList.add(new PullingStrings(this));
//        cardList.add(new TuggingStrings(this));
//        cardList.add(new AssailingPulls(this));
//        cardList.add(new ThinStrings(this));
//        cardList.add(new Puppetry(this));
        for (int i = 0; i < 10; i++) {
            cardList.add(new Madness());
        }
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    public void transitionToPhase2() {
        currentPhase = PHASE.PHASE2;
        numAdditionalMoves++;
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, POWER_DEBUFF) {
            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (damageAmount > 0 && info.owner == owner && info.type == DamageInfo.DamageType.NORMAL) {
                    applyToTarget(target, owner, new StrengthPower(target, -amount));
                    applyToTarget(target, owner, new DexterityPower(target, -amount));
                }
            }
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
    }

    @Override
    public void usePreBattleAction() {
        if (!usedPreBattleAction) {
            usedPreBattleAction = true;
        }
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Baral) {
                baral = (Baral)mo;
            }
        }
        this.powers.add(new Mystery(this));
        this.powers.add(new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case LINE: {
                block(this, BLOCK);
                slashAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case THIN_LINE: {
                pierceAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new StrengthPower(target, -DEBUFF));
                applyToTarget(target, this, new DexterityPower(target, -DEBUFF));
                resetIdle();
                break;
            }
            case THICK_LINE: {
                bluntAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new WeakPower(target, THICK_LINE_DEBUFF, true));
                applyToTarget(target, this, new VulnerablePower(target, THICK_LINE_DEBUFF, true));
                resetIdle();
                break;
            }
            case SHOCKWAVE: {
                int[] damageArray = new int[AbstractDungeon.getMonsters().monsters.size() + 1];
                info.applyPowers(this, adp());
                damageArray[damageArray.length - 1] = info.output;
                for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                    info.applyPowers(this, mo);
                    damageArray[i] = info.output;
                }

                massAttackStartAnimation();
                waitAnimation();
                massAttackFinishAnimation();
                atb(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        massAttackCooldown = MASS_ATTACK_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "ZenaNormalLine", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "ZenaThinLine", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "ZenaBoldLine", enemy, this);
    }

    private void massAttackStartAnimation() {
        animationAction("Shockwave1", "ZenaStart", this);
    }

    private void massAttackFinishAnimation() {
        animationAction("Shockwave2", "ZenaBoom", this);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) {
            firstMove = false;
        }
        atb(new RemoveAllBlockAction(this, this));
        if (currentPhase == PHASE.PHASE1 && gebura != null && !gebura.isDead && !gebura.isDying) {
            takeCustomTurn(this.moves.get(nextMove), gebura);
        } else {
            takeCustomTurn(this.moves.get(nextMove), adp());
        }
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            if (additionalIntent.targetTexture == null) {
                takeCustomTurn(additionalMove, adp());
            } else {
                takeCustomTurn(additionalMove, gebura);
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                massAttackCooldown--;
                this.isDone = true;
            }
        });
        if(currentPhase.equals(Zena.PHASE.PHASE1)){
            switch (GameActionManager.turn){
                case 4:
                    waitAnimation();
                    atb(new HeadDialogueAction(12, 13));
                    BinahHead binah = new BinahHead(-1250.0f, 0.0f);
                    atb(new SpawnMonsterAction(binah, false));
                    atb(new UsePreBattleActionAction(binah));
                    binah.onEntry();
                    atb(new HeadDialogueAction(14, 24));
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            binah.resetIdle(0.0f);
                            CustomDungeon.playTempMusicInstantly("TheHead");
                            isDone = true;
                        }
                    });
                    break;
                case 6:
                    waitAnimation();
                    atb(new HeadDialogueAction(25, 30));
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.MED, false);
                            playSound("Shaking");
                            this.isDone = true;
                        }
                    });
                    waitAnimation(1.0f);
                    float playerX = 1700.0f;
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            adp().drawX = playerX;
                            adp().flipHorizontal = true;
                            AbstractPower strength = adp().getPower(StrengthPower.POWER_ID);
                            if (strength != null) {
                                baral.roland.powers.add(strength);
                            }
                            adp().powers.clear();
                            this.isDone = true;
                        }
                    });
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            baral.useStaggerAnimation();
                            Zena.this.useStaggerAnimation();
                            AbstractDungeon.effectList.add(new StrikeEffect(baral, baral.hb.cX, baral.hb.cY, 999));
                            AbstractDungeon.effectList.add(new StrikeEffect(Zena.this, Zena.this.hb.cX, Zena.this.hb.cY, 999));
                            this.isDone = true;
                        }
                    });

                    atb(new SpawnMonsterAction(baral.roland, false));
                    atb(new UsePreBattleActionAction(baral.roland));
                    atb(new VFXAction(new SmokeBombEffect(playerX, adp().hb.cY), 1.0f));
                    atb(new HeadDialogueAction(31, 37));
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            transitionToPhase2();
                            baral.transitionToPhase2();
                            adp().drawPile.initializeDeck(adp().masterDeck);
                            adp().hand.clear();
                            adp().discardPile.clear();
                            adp().exhaustPile.clear();
                            adp().applyPreCombatLogic();
                            adp().applyStartOfCombatLogic();
                            adp().applyStartOfCombatPreDrawLogic();
                            baral.roland.rollMove();
                            baral.roland.currentHealth = adp().currentHealth;
                            baral.roland.healthBarUpdatedEvent();
                            adp().maxHealth = baral.playerMaxHp;
                            adp().currentHealth = baral.playerCurrentHp;
                            adp().healthBarUpdatedEvent();
                            fixOrbPositioning();
                            this.isDone = true;
                        }
                    });
                    break;
            }
        }
        atb(new RollMoveAction(this));
    }

    private void fixOrbPositioning() {
        for (int i = 0; i < AbstractDungeon.player.orbs.size(); i++) {
            (AbstractDungeon.player.orbs.get(i)).setSlot(i, AbstractDungeon.player.maxOrbs);
        }
    }

    @Override
    protected void getMove(final int num) {
        if (currentPhase == PHASE.PHASE1) {
            if (halfDead) {
                setMoveShortcut(NONE);
            } else if (massAttackCooldown <= 0) {
                setMoveShortcut(SHOCKWAVE, MOVES[SHOCKWAVE], cardList.get(SHOCKWAVE).makeStatEquivalentCopy());
            } else {
                ArrayList<Byte> possibilities = new ArrayList<>();
                if (!this.lastMove(THIN_LINE)) {
                    possibilities.add(THIN_LINE);
                }
                if (!this.lastMove(LINE)) {
                    possibilities.add(LINE);
                }
                byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
                setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
            }
        } else {
            if (massAttackCooldown <= 0) {
                setMoveShortcut(SHOCKWAVE, MOVES[SHOCKWAVE], cardList.get(SHOCKWAVE).makeStatEquivalentCopy());
            } else {
                ArrayList<Byte> possibilities = new ArrayList<>();
                if (!this.lastMove(THIN_LINE)) {
                    possibilities.add(THIN_LINE);
                }
                if (!this.lastMove(THICK_LINE) && !this.lastMoveBefore(THICK_LINE)) {
                    possibilities.add(THICK_LINE);
                }
                if (!this.lastMove(LINE)) {
                    possibilities.add(LINE);
                }
                byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
                setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
            }
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (moveHistory.size() >= 3) {
            moveHistory.clear(); //resetss cooldowns
        }
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(THIN_LINE, moveHistory) && !this.lastMoveBefore(THIN_LINE, moveHistory)) {
            possibilities.add(THIN_LINE);
        }
        if (!this.lastMove(THICK_LINE, moveHistory) && !this.lastMoveBefore(THICK_LINE, moveHistory)) {
            possibilities.add(THICK_LINE);
        }
        if (!this.lastMove(LINE, moveHistory) && !this.lastMoveBefore(LINE, moveHistory)) {
            possibilities.add(LINE);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1) {
            super.applyPowers();
            return;
        }
        if (currentPhase == PHASE.PHASE1 && gebura != null && !gebura.isDead && !gebura.isDying && nextMove != SHOCKWAVE) {
            DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            AbstractCreature target = gebura;
            if (info.base > -1) {
                info.applyPowers(this, target);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                PowerTip intentTip = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                int multiplier = moves.get(this.nextMove).multiplier;
                Texture attackImg;
                if (multiplier > 0) {
                    attackImg = getAttackIntent(info.output * multiplier);
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[3] + multiplier + TEXT[4];
                } else {
                    attackImg = getAttackIntent(info.output);
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[2];
                }
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentImg", attackImg);
            }
        } else {
            super.applyPowers();
            for (int i = 0; i < additionalIntents.size(); i++) {
                AdditionalIntent additionalIntent = additionalIntents.get(i);
                EnemyMoveInfo additionalMove = null;
                if (i < additionalMoves.size()) {
                    additionalMove = additionalMoves.get(i);
                }
                if (additionalMove != null) {
                    applyPowersToAdditionalIntent(additionalMove, additionalIntent, gebura, gebura.allyIcon);
                }
            }
        }
    }

    @Override
    public void renderIntent(SpriteBatch sb) {
        super.renderIntent(sb);
        if (currentPhase == PHASE.PHASE1 && gebura != null && !gebura.isDead && !gebura.isDying && !this.isDeadOrEscaped() && nextMove != SHOCKWAVE) {
            BobEffect bobEffect = ReflectionHacks.getPrivate(this, AbstractMonster.class, "bobEffect");
            float intentAngle = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentAngle");
            sb.draw(TexLoader.getTexture(gebura.allyIcon), this.intentHb.cX - 48.0F, this.intentHb.cY - 48.0F + (40.0f * Settings.scale) + bobEffect.y, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, intentAngle, 0, 0, 48, 48, false, false);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if(currentPhase == PHASE.PHASE1){
            if (!this.isDead && !this.escaped) {
                if (this.animation != null && this.animation.type() == AbstractAnimation.Type.SPRITE) {
                    this.animation.renderSprite(sb, this.drawX + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY);
                } else if (this.atlas == null) {
                    sb.setColor(this.tint.color);
                    sb.draw(this.img, this.drawX - (float)this.img.getWidth() * Settings.scale / 2.0F + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY, (float)this.img.getWidth() * Settings.scale, (float)this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
                } else {
                    this.state.update(Gdx.graphics.getDeltaTime());
                    this.state.apply(this.skeleton);
                    this.skeleton.updateWorldTransform();
                    this.skeleton.setPosition(this.drawX + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY);
                    this.skeleton.setColor(this.tint.color);
                    this.skeleton.setFlip(this.flipHorizontal, this.flipVertical);
                    sb.end();
                    CardCrawlGame.psb.begin();
                    AbstractMonster.sr.draw(CardCrawlGame.psb, this.skeleton);
                    CardCrawlGame.psb.end();
                    sb.begin();
                    sb.setBlendFunction(770, 771);
                }

                if (this == AbstractDungeon.getCurrRoom().monsters.hoveredMonster && this.atlas == null && this.animation == null) {
                    sb.setBlendFunction(770, 1);
                    sb.setColor(new Color(1.0F, 1.0F, 1.0F, 0.1F));
                    sb.draw(this.img, this.drawX - (float)this.img.getWidth() * Settings.scale / 2.0F + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY, (float)this.img.getWidth() * Settings.scale, (float)this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
                    sb.setBlendFunction(770, 771);
                }

                if (!this.isDying && !this.isEscaping && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.player.isDead && !AbstractDungeon.player.hasRelic("Runic Dome") && this.intent != Intent.NONE && !Settings.hideCombatElements) {
                    this.renderIntentVfxBehind(sb);
                    this.renderIntent(sb);
                    this.renderIntentVfxAfter(sb);
                    this.renderDamageRange(sb);
                }
                this.intentHb.render(sb);
            }
        } else {
            super.render(sb);
        }
    }

}