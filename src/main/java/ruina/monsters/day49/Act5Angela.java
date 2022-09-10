package ruina.monsters.day49;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.ClearCardQueueAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.cards.green.DaggerSpray;
import com.megacrit.cardcrawl.cards.green.Terror;
import com.megacrit.cardcrawl.cards.red.Bludgeon;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.Day49PhaseTransition4Action;
import ruina.actions.Day49PhaseTransition5Action;
import ruina.actions.SilentGirlEffectAction;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.act3.silentGirl.DummyHammer;
import ruina.monsters.act3.silentGirl.DummyNail;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.TreeOfLifeManager;
import ruina.monsters.theHead.Baral;
import ruina.powers.DamageReductionInvincible;
import ruina.powers.Refracting;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.io.IOException;
import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.atb;

public class Act5Angela extends AbstractCardMonster {
    public static final String ID = makeID(Act5Angela.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;


    private static final byte CRACKED_HEART = 0;
    private final int crackedHeartDamage = 15;
    private final int crackedHeartFrail = 2;

    private static final byte COLLAPSING_HEART = 1;
    private final int collapsingHeartDamage = 30;
    private final int collapsingHeartVulnerable = 1;

    private static final byte BROKEN = 3;
    private final int brokenDamage = 50;

    private static final byte PHASE_TRANSITION = 4;

    private final int HP = 5000;
    private int turnCounter = 1; //1 is first turn, 2 is second turn, 3rd is third turn (transition phase)

    private DummyHammer hammer = new DummyHammer(100.0f, 0.0f);
    private DummyNail nail = new DummyNail(-300.0f, 0.0f);


    public Act5Angela() {
        this(-100.0f, 0.0f);
    }

    public Act5Angela(final float x, final float y) {
        super(NAME, ID, 480, 0.0F, 0, 250.0f, 290.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Day49/Remorse/Spriter/SilentGirl.scml"));
        this.type = EnemyType.BOSS;
        setHp(1);
        addMove(CRACKED_HEART, Intent.ATTACK_DEBUFF, crackedHeartDamage);
        addMove(COLLAPSING_HEART, Intent.ATTACK_DEBUFF, collapsingHeartDamage);
        addMove(BROKEN, Intent.ATTACK, brokenDamage);
        addMove(PHASE_TRANSITION, Intent.UNKNOWN);

        hideHealthBar();
        populateCards();
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.scene.nextRoom(AbstractDungeon.getCurrRoom());
        (AbstractDungeon.getCurrRoom()).cannotLose = true;
        CustomDungeon.playTempMusicInstantly("Story2");
        atb(new ApplyPowerAction(this, this, new Refracting(this, -1)));
        atb(new ApplyPowerAction(this, this, new DamageReductionInvincible(this, HP / 4)));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if (info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case CRACKED_HEART: {
                hammer.attackAnimation(adp());
                dmg(adp(), info);
                hammer.resetIdle();
                applyToTarget(adp(), this, new FrailPower(adp(), crackedHeartFrail, true));
                break;
            }
            case COLLAPSING_HEART: {
                hammer.attackAnimation(adp());
                dmg(adp(), info);
                hammer.resetIdle();
                applyToTarget(adp(), this, new VulnerablePower(adp(), collapsingHeartVulnerable, true));
                break;
            }
            case BROKEN: {
                specialDownAnimation(adp());
                atb(new SilentGirlEffectAction(true));
                dmg(adp(), info);
                resetIdle();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(!RuinaMod.ruinaConfig.getBool("seenD49Message")){
                            RuinaMod.ruinaConfig.setBool("seenD49Message", true);
                            try { RuinaMod.ruinaConfig.save(); } catch (IOException e) { e.printStackTrace(); }
                            att(new Day49PhaseTransition5Action(0, 5, Act5Angela.this));
                        }
                        else { att(new Day49PhaseTransition5Action(0, 2, Act5Angela.this)); }
                        isDone = true;
                    }
                });
                break;
            }
            case PHASE_TRANSITION:
                // Same Animation, No damage
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        runAnim("SpecialDown");
                        playSound("SilentHammer");
                        isDone = true;
                    }
                });
                atb(new SilentGirlEffectAction(true));
                resetIdle();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(!RuinaMod.ruinaConfig.getBool("seenD49Message")){
                            RuinaMod.ruinaConfig.setBool("seenD49Message", true);
                            try { RuinaMod.ruinaConfig.save(); } catch (IOException e) { e.printStackTrace(); }
                            att(new Day49PhaseTransition5Action(0, 5, Act5Angela.this));
                        }
                        else { att(new Day49PhaseTransition5Action(0, 2, Act5Angela.this)); }
                        isDone = true;
                    }
                });
                break;

        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                turnCounter += 1;
                isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if(halfDead){ setMoveShortcut(PHASE_TRANSITION); }
        else {
            if (turnCounter == 3) {
                specialUpAnimation(adp());
                setMoveShortcut(BROKEN, MOVES[BROKEN], cardList.get(BROKEN));
            } else if (turnCounter == 2) {
                setMoveShortcut(COLLAPSING_HEART, MOVES[COLLAPSING_HEART], cardList.get(COLLAPSING_HEART));
            } else {
                setMoveShortcut(CRACKED_HEART, MOVES[CRACKED_HEART], cardList.get(CRACKED_HEART));
            }
        }
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle1");
                this.isDone = true;
            }
        });
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (!this.isDeadOrEscaped()) {
            hammer.render(sb);
            nail.render(sb);
        }
    }

    private void populateCards() {
        cardList.add(new Terror());
        cardList.add(new Madness());
        cardList.add(new Bludgeon());
        cardList.add(new DaggerSpray());
        cardList.add(new Terror());
        cardList.add(new Terror());
        cardList.add(new Terror());
        cardList.add(new Terror());
        cardList.add(new Terror());
    }

    public void damage(DamageInfo info) {
        // Shouldn't be possible - but just in case
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            cardsToRender.clear();
            AbstractCardMonster.hoveredCard = null;
            this.halfDead = true;
            for (AbstractPower p : this.powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            att(new ClearCardQueueAction());
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power instanceof Refracting)) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }
            cardsToRender.clear();
            setMove(PHASE_TRANSITION, Intent.UNKNOWN);
            additionalIntents.clear();
            additionalMoves.clear();
            ArrayList<AbstractCard> cards = cardsToRender;
            if (cards.size() > 1) {
                cards.remove(cards.size() - 1);
            }
            createIntent();
        }
    }

    public void die() {
        if (!(AbstractDungeon.getCurrRoom()).cannotLose) {
            super.die();
        }
    }

    public void dieBypass() {
        super.die(false);
    }

    private void rangedAnimation(AbstractCreature enemy) {
        animationAction("Ranged", "SilentEye", enemy, this);
    }

    private void phaseChangeAnimation() {
        animationAction("Ranged", "SilentPhaseChange", this);
    }

    private void specialUpAnimation(AbstractCreature enemy) {
        animationAction("SpecialUp", "SilentHammer", enemy, this);
    }

    private void specialDownAnimation(AbstractCreature enemy) {
        animationAction("SpecialDown", "SilentHammer", enemy, this);
    }

    public void transitionToSecondPart() {
        AbstractDungeon.bossKey = TreeOfLifeManager.ID;
        CardCrawlGame.music.fadeOutBGM();
        CardCrawlGame.music.fadeOutTempBGM();
        MapRoomNode node = new MapRoomNode(-1, 15);
        node.room = new MonsterRoomBoss();
        AbstractDungeon.nextRoom = node;
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.nextRoomTransitionStart();
    }
}
