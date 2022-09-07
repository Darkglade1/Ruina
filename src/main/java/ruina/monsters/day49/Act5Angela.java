package ruina.monsters.day49;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
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
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.act3.silentGirl.DummyHammer;
import ruina.monsters.act3.silentGirl.DummyNail;
import ruina.monsters.theHead.Baral;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

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
        setHp(5000);
        addMove(CRACKED_HEART, Intent.ATTACK_DEBUFF, crackedHeartDamage);
        addMove(COLLAPSING_HEART, Intent.ATTACK_DEBUFF, collapsingHeartDamage);
        addMove(BROKEN, Intent.ATTACK, brokenDamage);

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
        CustomDungeon.playTempMusicInstantly("Story2");
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
                dmg(adp(), info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        transitionToPhase2Idea();
                        isDone = true;
                    }
                });
                resetIdle();
                break;
            }
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
        if (turnCounter == 3) {
            specialUpAnimation(adp());
            setMoveShortcut(BROKEN, MOVES[BROKEN], cardList.get(BROKEN));
        } else if (turnCounter == 2) {
            setMoveShortcut(COLLAPSING_HEART, MOVES[COLLAPSING_HEART], cardList.get(COLLAPSING_HEART));
        } else {
            setMoveShortcut(CRACKED_HEART, MOVES[CRACKED_HEART], cardList.get(CRACKED_HEART));
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
        animationAction("SpecialUp", null, enemy, this);
    }

    private void specialDownAnimation(AbstractCreature enemy) {
        animationAction("SpecialDown", "SilentHammer", enemy, this);
    }

    private void transitionToPhase2Idea() {
        AbstractDungeon.bossKey = Baral.ID;
        CardCrawlGame.music.fadeOutBGM();
        CardCrawlGame.music.fadeOutTempBGM();
        MapRoomNode node = new MapRoomNode(-1, 15);
        node.room = (AbstractRoom) new MonsterRoomBoss();
        AbstractDungeon.nextRoom = node;
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.nextRoomTransitionStart();
    }
}
