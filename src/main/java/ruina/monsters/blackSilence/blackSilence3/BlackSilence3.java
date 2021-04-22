package ruina.monsters.blackSilence.blackSilence3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.stances.WrathStance;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.BetterIntentFlashAction;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;
import ruina.monsters.blackSilence.blackSilence4.cards.Agony;
import ruina.monsters.blackSilence.blackSilence4.cards.Scream;
import ruina.monsters.blackSilence.blackSilence4.cards.Void;
import ruina.monsters.blackSilence.blackSilence4.memories.Shi.Shi;
import ruina.monsters.blackSilence.blackSilence4.memories.blue.Blue;
import ruina.monsters.blackSilence.blackSilence4.memories.dawn.Dawn;
import ruina.monsters.blackSilence.blackSilence4.memories.hana.Hana;
import ruina.monsters.blackSilence.blackSilence4.memories.liu.Liu;
import ruina.monsters.blackSilence.blackSilence4.memories.love.Love;
import ruina.monsters.blackSilence.blackSilence4.memories.purple.Purple;
import ruina.monsters.blackSilence.blackSilence4.memories.yun.Yun;
import ruina.monsters.blackSilence.blackSilence4.memories.zwei.Zwei;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Argalia;
import ruina.powers.BlackSilence;
import ruina.util.AdditionalIntent;
import ruina.vfx.FlexibleStanceAuraEffect;
import ruina.vfx.FlexibleWrathParticleEffect;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class BlackSilence3 extends AbstractCardMonster {

    public static final String ID = RuinaMod.makeID(BlackSilence3.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public AbstractCard bond;
    public AbstractCard waltz;

    public boolean rolandAttackedThisTurn = false;

    private static final byte UNITED_WORKSHOP = 0;
    private static final byte LONELINESS = 1;
    private static final byte FURY = 2;
    private static final byte WALTZ = 3;
    private static final byte DARKBOND = 4;
    private static final byte NONE = 5;
    private static final byte SOUL_LINK_REVIVAL = 6;

    private final int unitedDamage = calcAscensionDamage(7);
    private final int unitedHits = 2;
    private final int lonelyDamage = calcAscensionDamage(15);
    public final int lonelyDebuff = calcAscensionSpecial(2);
    public final int furyStrength = 2;
    private final int waltzDamage = calcAscensionDamage(30);
    private final int bondStrength = calcAscensionSpecial(3);
    private static final byte TURNS_UNTIL_WALTZ = 3;
    private int turn = TURNS_UNTIL_WALTZ;
    private Angelica angelica;

    private float particleTimer;
    private float particleTimer2;

    public BlackSilence3() { this(70.0f, 10.0f); }
    public BlackSilence3(final float x, final float y) {
        super(NAME, ID, 500, 0.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BlackSilence4/Spriter/BlackSilence4.scml"));
        this.setHp(calcAscensionTankiness(this.maxHealth));
        this.type = EnemyType.BOSS;

        addMove(UNITED_WORKSHOP, Intent.ATTACK, unitedDamage, unitedHits, true);
        addMove(LONELINESS, Intent.ATTACK_DEBUFF, lonelyDamage);
        addMove(FURY, Intent.BUFF);
        addMove(WALTZ, Intent.ATTACK, waltzDamage);
        addMove(DARKBOND, Intent.BUFF);

        cardList.add(new Madness());
        cardList.add(new Madness());
        cardList.add(new Madness());
        cardList.add(new Madness());
        cardList.add(new Madness());
        cardList.add(new Madness());
        cardList.add(new Madness());
    }

    @Override
    public void takeTurn() {
        DamageInfo info;
        int multiplier = 0;
        if (moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else { info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL); }
        AbstractCreature target = adp();
        if (info.base > -1) { info.applyPowers(this, target); }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if(nextMove == WALTZ){ turn = TURNS_UNTIL_WALTZ; }
                else if(nextMove != NONE) { turn -= 1; }
                isDone = true;
            }
        });
        switch (this.nextMove) {
            case UNITED_WORKSHOP:
                for (int i = 0; i < multiplier; i++) { dmg(target, info); }
                break;
            case LONELINESS: {
                dmg(target, info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(adp().lastDamageTaken > 0){
                            applyToTargetTop(adp(), BlackSilence3.this, new WeakPower(adp(), lonelyDebuff, true));
                            applyToTargetTop(adp(), BlackSilence3.this, new FrailPower(adp(), lonelyDebuff, true));
                            applyToTargetTop(adp(), BlackSilence3.this, new VulnerablePower(adp(), lonelyDebuff, true));
                        }
                        isDone = true;
                    }
                });
                break;
            }
            case FURY:
                for(AbstractMonster m: monsterList()){ applyToTarget(m, this, new StrengthPower(m, furyStrength)); }
                break;
            case WALTZ: {
                for (int i = 0; i < multiplier; i++) { dmg(target, info); }
                break;
            }
            case DARKBOND:
                applyToTarget(angelica, this, new StrengthPower(angelica, bondStrength));
                break;
            case SOUL_LINK_REVIVAL:
                halfDead = false;
                atb(new HealAction(this, this, this.maxHealth));
                break;
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractPower power = BlackSilence3.this.getPower(ruina.powers.BlackSilence3.POWER_ID);
                if(power != null && power.amount == -1){
                    BlackSilence3.this.setEmptyMove();
                    createIntent();
                }
                else { att(new RollMoveAction(BlackSilence3.this)); }
                isDone = true;
            }
        });
    }


    @Override
    public void createIntent() {
        super.createIntent();
        applyPowers();
    }

    @Override
    protected void getMove(final int num) {
        if(turn == 0){ setMoveShortcut(WALTZ, MOVES[WALTZ], cardList.get(WALTZ).makeStatEquivalentCopy()); }
        else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(UNITED_WORKSHOP)) { possibilities.add(UNITED_WORKSHOP); }
            if (!this.lastTwoMoves(LONELINESS)) { possibilities.add(LONELINESS); }
            if (!this.lastTwoMoves(FURY)) { possibilities.add(FURY); }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
        }
    }

    public void usePreBattleAction(){
        (AbstractDungeon.getCurrRoom()).cannotLose = true;
        applyToTarget(this, this, new ruina.powers.BlackSilence3(this));
        AbstractDungeon.player.drawX += 480.0F * Settings.scale;
        AbstractDungeon.player.dialogX += 480.0F * Settings.scale;
        applyToTarget(adp(), this, new SurroundedPower(adp()));
        for (AbstractMonster mo : monsterList()) { if (mo instanceof Angelica) { angelica = (Angelica) mo; } }
    }

    public void setEmptyMove(){ setMove(NONE, Intent.NONE); }
    public void setBondIntent(){ setMoveShortcut(DARKBOND, MOVES[DARKBOND]); }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            for (AbstractPower p : this.powers) { p.onDeath(); }
            for (AbstractRelic r : AbstractDungeon.player.relics) { r.onMonsterDeath(this); }
            this.powers.clear();
            boolean allDead = true;
            for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                if (m instanceof Angelica && !m.halfDead) { allDead = false; }
            }
            System.out.println(allDead);
            if (!allDead) {
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        cardsToRender.clear();
                        setMove(SOUL_LINK_REVIVAL, Intent.UNKNOWN);
                        createIntent();
                        isDone = true;
                    }
                });
            } else {
                (AbstractDungeon.getCurrRoom()).cannotLose = false;
                this.halfDead = false;
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) { m.die(); }
            }
        }
    }

    public void die() { if (!(AbstractDungeon.getCurrRoom()).cannotLose) super.die(); }
    
}
