package ruina.monsters.day49.act5;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.cards.green.DaggerSpray;
import com.megacrit.cardcrawl.cards.green.Terror;
import com.megacrit.cardcrawl.cards.red.Bludgeon;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.*;
import ruina.BetterSpriterAnimation;
import ruina.cards.Guilt;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.act3.silentGirl.DummyHammer;
import ruina.monsters.act3.silentGirl.DummyNail;
import ruina.monsters.day49.Act4Angela;
import ruina.powers.*;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;
import java.util.Collections;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.atb;

public class Act5Angela1 extends AbstractCardMonster
{
    public static final String ID = makeID(Act5Angela1.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private final float HP_THRESHOLD = 0.5f;
    private final float DAMAGE_INCREASE = 0.5f;

    private static final byte LEER = 0;
    private final int leerBlock = 75;
    private final int leerStrength = 5;

    private static final byte COLLAPSING_HEART = 1;
    private final int collapsingHeartDamage = 20;
    private final int collapsingHeartHits = 2;
    private final int collapsingHeartParalysis = 3;

    private static final byte CRACKED_HEART = 2;
    private final int crackedHeartDamage = 20;
    private final int crackedHeartHits = 2;
    private final int crackedHeartBleed = 3;

    private static final byte REND = 3;
    private final int rendDamage = 19;
    private final int rendHits = 3;
    private final int rendWeak = 2;

    private static final byte IMPALE = 4;
    private final int impaleDamage = 19;
    private final int impaleHits = 3;
    private final int impaleFrail = 2;

    private static final byte BROKEN = 5;
    private final int brokenDamage = 25;
    public int brokenHits = 1;
    private final int brokenHitsIncrease = 1;

    private static final byte DESPERATION = 6;
    private final int desperationWeak = 5;
    private final int desperationFrail = 5;

    private int enraged = 1; //1 is false, 2 is true
    private boolean usedDesperation = false;
    private enum STATE {
        HAMMER,
        NAIL,
        BOTH
    }

    private final ArrayList<Byte> movepool = new ArrayList<>();
    private Act5Hammer hammer = new Act5Hammer(100.0f, 0.0f);
    private Act5Nail nail = new Act5Nail(-300.0f, 0.0f);

    AbstractCard curse = new Guilt();
    private STATE currentSTATE;
    public static final String POWER_ID = makeID("Remorse");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Act5Angela1() {
        this(-100.0f, 0.0f);
    }

    public Act5Angela1(final float x, final float y) {
        super(NAME, ID, 480, 0.0F, 0, 250.0f, 290.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SilentGirl/Spriter/SilentGirl.scml"));
        this.type = EnemyType.BOSS;
        setHp(5000);
        addMove(LEER, Intent.DEFEND, leerBlock);

        addMove(CRACKED_HEART, Intent.ATTACK_DEBUFF, crackedHeartDamage, crackedHeartHits, true);
        addMove(COLLAPSING_HEART, Intent.ATTACK_DEBUFF, crackedHeartDamage, crackedHeartHits, true);

        addMove(REND, Intent.ATTACK_DEBUFF, rendDamage, rendHits, true);
        addMove(IMPALE, Intent.ATTACK_DEBUFF, impaleDamage, impaleHits, true);

        addMove(BROKEN, Intent.ATTACK, brokenDamage, brokenHits, true);
        addMove(DESPERATION, Intent.STRONG_DEBUFF);

        currentSTATE = STATE.BOTH;
        populateCards();
        populateMovepool();
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Story2");
        atb(new ApplyPowerAction(this, this, new RighteousHammerPiercingNail(this)));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                att(new ApplyPowerAction(Act5Angela1.this, Act5Angela1.this, new RighteousHammer(Act5Angela1.this)));
                currentSTATE = STATE.HAMMER;
                isDone = true;
            }
        });
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case COLLAPSING_HEART: {
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                }
                applyToTarget(adp(), this, new Paralysis(adp(), collapsingHeartParalysis));
                nail.resetIdle();
                break;
            }
            case CRACKED_HEART: {
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                }
                applyToTarget(adp(), this, new Bleed(adp(), crackedHeartBleed));
                nail.resetIdle();
                break;
            }
            case REND: {
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                }
                applyToTarget(adp(), this, new WeakPower(adp(), rendWeak, true));
                nail.resetIdle();
                break;
            }
            case IMPALE: {
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                }
                applyToTarget(adp(), this, new FrailPower(adp(), impaleFrail, true));
                nail.resetIdle();
                break;
            }
            case BROKEN: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        specialUpAnimation(adp());
                    } else {
                        specialDownAnimation(adp());
                    }
                    dmg(adp(), info);
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            brokenHits += brokenHitsIncrease;
                            isDone = true;
                        }
                    });
                    resetIdle();
                }
                break;
            }
            case LEER: {
                rangedAnimation(adp());
                resetIdle(1.0f);
                break;
            }
            case DESPERATION: {
                usedDesperation = true;
                currentSTATE = STATE.BOTH;
                phaseChangeAnimation();
                hammer.deadAnimation();
                nail.deadAnimation();
                atb(new RemoveDebuffsAction(this));
                if (AbstractDungeon.ascensionLevel >= 19) { atb(new HealAction(this, this, (int)(maxHealth * 0.25f))); }
                resetIdle(1.0f);
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if(Act5Angela1.this.currentHealth <= Act5Angela1.this.maxHealth / 2){ enraged = 2; }
                isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (enraged == 2) {
            if(!usedDesperation){ setMoveShortcut(DESPERATION); }
            else{
                setMoveShortcut(BROKEN, MOVES[BROKEN], cardList.get(BROKEN));
            }
        } else {
            if (movepool.isEmpty()) { populateMovepool(); }
            byte intent = movepool.remove(0);
            setMoveShortcut(intent, MOVES[intent], cardList.get(intent));
        }
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + enraged);
                this.isDone = true;
            }
        });
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (!this.isDeadOrEscaped()) {
            if(currentSTATE == STATE.HAMMER){
                hammer.render(sb);
            }
            else if(currentSTATE == STATE.NAIL){
                nail.render(sb);
            }
            else {
                hammer.render(sb);
                nail.render(sb);
            }
        }
    }

    @Override
    public void update() {
        super.update();
        if(InputHelper.justClickedRight && hb.hovered){
            switch (currentSTATE){
                case NAIL:
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            AbstractPower p = Act5Angela1.this.getPower(PiercingNail.POWER_ID);
                            if(p != null){
                                makePowerRemovable(p);
                                att(new RemoveSpecificPowerAction(Act5Angela1.this, Act5Angela1.this, p));
                            }
                            att(new ApplyPowerAction(Act5Angela1.this, Act5Angela1.this, new RighteousHammer(Act5Angela1.this)));
                            currentSTATE = STATE.HAMMER;
                            isDone = true;
                        }
                    });
                    break;
                case HAMMER:
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            AbstractPower p = Act5Angela1.this.getPower(RighteousHammer.POWER_ID);
                            if(p != null){
                                makePowerRemovable(p);
                                att(new RemoveSpecificPowerAction(Act5Angela1.this, Act5Angela1.this, p));
                            }
                            att(new ApplyPowerAction(Act5Angela1.this, Act5Angela1.this, new PiercingNail(Act5Angela1.this)));
                            currentSTATE = STATE.NAIL;
                            isDone = true;
                        }
                    });
                    break;
            }
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

    private void populateMovepool() {
        if(AbstractDungeon.monsterRng.random(0, 1) == 1){
            movepool.add(COLLAPSING_HEART);
            movepool.add(IMPALE);
        }
        else {
            movepool.add(CRACKED_HEART);
            movepool.add(REND);
        }
        movepool.add(LEER);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        onBossVictoryLogic();
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

}
