package ruina.monsters.theHead;

import actlikeit.dungeons.CustomDungeon;
import basemod.animations.AbstractAnimation;
import basemod.helpers.CardModifierManager;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.InvinciblePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.cardmods.BlackSilenceRenderMod;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.theHead.dialogue.TestHeadDialogue;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_FURIOSO;
import ruina.monsters.uninvitedGuests.normal.pluto.monster.Pluto;
import ruina.monsters.uninvitedGuests.normal.puppeteer.Chesed;
import ruina.monsters.uninvitedGuests.normal.puppeteer.Puppet;
import ruina.monsters.uninvitedGuests.normal.puppeteer.puppeteerCards.AssailingPulls;
import ruina.monsters.uninvitedGuests.normal.puppeteer.puppeteerCards.PullingStrings;
import ruina.monsters.uninvitedGuests.normal.puppeteer.puppeteerCards.Puppetry;
import ruina.monsters.uninvitedGuests.normal.puppeteer.puppeteerCards.ThinStrings;
import ruina.monsters.uninvitedGuests.normal.puppeteer.puppeteerCards.TuggingStrings;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.PlayerBlackSilence;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;
import java.util.function.BiFunction;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Baral extends AbstractCardMonster
{
    public static final String ID = makeID(Baral.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte SERUM_W = 0;
    private static final byte TRAIL = 1;
    private static final byte EXTIRPATION = 2;
    private static final byte TRI_SERUM_COCKTAIL = 3;
    private static final byte SERUM_K = 4;

    public final int trailHits = 2;
    public final int triSerumHits = 3;

    private static final int SERUM_COOLDOWN = 3;
    private int serumCooldown = SERUM_COOLDOWN;

    public final int BLOCK = calcAscensionTankiness(50);
    public final int SERUM_K_BLOCK = calcAscensionTankiness(60);
    public final int HEAL = calcAscensionTankiness(100);
    public final int STRENGTH = calcAscensionSpecial(3);

    public RolandHead roland;
    //public Zena zena;

    public enum PHASE{
        PHASE1,
        PHASE2
    }

    public PHASE currentPhase = PHASE.PHASE1;

    public Baral() {
        this(0.0f, 0.0f, PHASE.PHASE1);
    }

    public Baral(final float x, final float y, PHASE phase) {
        super(NAME, ID, 999999, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Puppeteer/Spriter/Puppeteer.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 0;
        maxAdditionalMoves = 1;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(maxHealth));
        currentPhase = phase;
        addMove(SERUM_W, Intent.ATTACK, calcAscensionDamage(45));
        addMove(TRAIL, Intent.ATTACK_BUFF, calcAscensionDamage(12), trailHits, true);
        addMove(EXTIRPATION, Intent.ATTACK_DEFEND, calcAscensionDamage(26));
        addMove(TRI_SERUM_COCKTAIL, Intent.ATTACK, calcAscensionDamage(10), triSerumHits, true);
        addMove(SERUM_K, Intent.DEFEND_BUFF);

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

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Ensemble2");
//        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
//            if (mo instanceof Zena) {
//                zena = (Zena)mo;
//            }
//        }
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        //applyToTarget(this, this, new InvinciblePower(this, 100));
        AbstractDungeon.topLevelEffectsQueue.add(new TestHeadDialogue());
        roland = new RolandHead(-1700.0F, -20.0f);
        roland.drawX = AbstractDungeon.player.drawX;
        AbstractPower power = new PlayerBlackSilence(adp(), roland);
        AbstractDungeon.player.powers.add(power);

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.player.drawPile.group.clear();
                AbstractDungeon.player.discardPile.group.clear();
                AbstractDungeon.player.exhaustPile.group.clear();
                AbstractDungeon.player.hand.group.clear();
                this.isDone = true;
            }
        });
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractCard card : roland.cardList) {
                    if (!card.cardID.equals(CHRALLY_FURIOSO.ID)) {
                        CardModifierManager.addModifier(card, new BlackSilenceRenderMod());
                        AbstractDungeon.player.drawPile.group.add(card.makeStatEquivalentCopy());
                    }
                }
                AbstractDungeon.player.drawPile.shuffle();
                this.isDone = true;
            }
        });
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

            if (roland != null) {
                roland.render(sb);
            }
        }
        else {
            super.render(sb);
        }

    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case SERUM_W: {
                pierceAnimation(target);
                dmg(target, info);
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        serumCooldown = SERUM_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case TRAIL: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        bluntAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                break;
            }
            case EXTIRPATION: {
                block(this, BLOCK);
                rangedAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case TRI_SERUM_COCKTAIL: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        bluntAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case SERUM_K: {
                buffAnimation();
                block(this, SERUM_K_BLOCK);
                atb(new HealAction(this, this, HEAL));
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "BluntHori", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "BluntBlow", enemy, this);
    }

    private void rangedAnimation(AbstractCreature enemy) {
        animationAction("Ranged", "PuppetBreak", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

    private void buffAnimation() {
        animationAction("Special", null, this);
    }

    private void massAttackStartAnimation() {
        animationAction("Special", "PuppetStart", this);
    }

    private void massAttackFinishAnimation() {
        animationAction("Special", "PuppetStrongAtk", this);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) {
            firstMove = false;
        }
        atb(new RemoveAllBlockAction(this, this));
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            if (additionalIntent.targetTexture == null) {
                takeCustomTurn(additionalMove, adp());
            } else {
                takeCustomTurn(additionalMove, roland);
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                serumCooldown--;
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (serumCooldown <= 0) {
            setMoveShortcut(SERUM_W, MOVES[SERUM_W], cardList.get(SERUM_W).makeStatEquivalentCopy());
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(TRAIL)) {
                possibilities.add(TRAIL);
            }
            if (!this.lastMove(EXTIRPATION)) {
                possibilities.add(EXTIRPATION);
            }
            if (!this.lastMove(TRI_SERUM_COCKTAIL)) {
                possibilities.add(TRI_SERUM_COCKTAIL);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (this.lastMove(EXTIRPATION, moveHistory)) {
            setAdditionalMoveShortcut(TRAIL, moveHistory, cardList.get(TRAIL).makeStatEquivalentCopy());
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(TRAIL, moveHistory)) {
                possibilities.add(TRAIL);
            }
            if (!this.lastMove(EXTIRPATION, moveHistory)) {
                possibilities.add(EXTIRPATION);
            }
            if (!this.lastMove(SERUM_K, moveHistory)) {
                possibilities.add(SERUM_K);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, roland, roland.allyIcon);
            }
        }
    }

}