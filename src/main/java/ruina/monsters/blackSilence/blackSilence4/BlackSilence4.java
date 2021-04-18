package ruina.monsters.blackSilence.blackSilence4;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.InvinciblePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.BetterIntentFlashAction;
import ruina.cards.EGO.act2.Mimicry;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.blackSilence.blackSilence4.cards.Agony;
import ruina.monsters.blackSilence.blackSilence4.cards.Scream;
import ruina.monsters.blackSilence.blackSilence4.cards.Void;
import ruina.monsters.blackSilence.blackSilence4.memories.Shi.Shi;
import ruina.monsters.blackSilence.blackSilence4.memories.Shi.Shi1;
import ruina.monsters.blackSilence.blackSilence4.memories.Shi.Shi2;
import ruina.monsters.blackSilence.blackSilence4.memories.blue.Blue;
import ruina.monsters.blackSilence.blackSilence4.memories.blue.Blue1;
import ruina.monsters.blackSilence.blackSilence4.memories.blue.Blue2;
import ruina.monsters.blackSilence.blackSilence4.memories.dawn.Dawn;
import ruina.monsters.blackSilence.blackSilence4.memories.dawn.Dawn1;
import ruina.monsters.blackSilence.blackSilence4.memories.dawn.Dawn2;
import ruina.monsters.blackSilence.blackSilence4.memories.hana.Hana;
import ruina.monsters.blackSilence.blackSilence4.memories.hana.Hana1;
import ruina.monsters.blackSilence.blackSilence4.memories.hana.Hana2;
import ruina.monsters.blackSilence.blackSilence4.memories.liu.Liu;
import ruina.monsters.blackSilence.blackSilence4.memories.liu.Liu1;
import ruina.monsters.blackSilence.blackSilence4.memories.liu.Liu2;
import ruina.monsters.blackSilence.blackSilence4.memories.love.Love;
import ruina.monsters.blackSilence.blackSilence4.memories.love.Love1;
import ruina.monsters.blackSilence.blackSilence4.memories.love.Love2;
import ruina.monsters.blackSilence.blackSilence4.memories.purple.Purple;
import ruina.monsters.blackSilence.blackSilence4.memories.purple.Purple1;
import ruina.monsters.blackSilence.blackSilence4.memories.purple.Purple2;
import ruina.monsters.blackSilence.blackSilence4.memories.yun.Yun;
import ruina.monsters.blackSilence.blackSilence4.memories.yun.Yun1;
import ruina.monsters.blackSilence.blackSilence4.memories.yun.Yun2;
import ruina.monsters.blackSilence.blackSilence4.memories.zwei.Zwei;
import ruina.monsters.blackSilence.blackSilence4.memories.zwei.Zwei1;
import ruina.monsters.blackSilence.blackSilence4.memories.zwei.Zwei2;
import ruina.monsters.uninvitedGuests.normal.puppeteer.Puppet;
import ruina.powers.Scars;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class BlackSilence4 extends AbstractCardMonster {
    public static final String ID = RuinaMod.makeID(BlackSilence4.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte AGONY = 0;
    private static final byte SCREAM = 1;
    private static final byte VOID = 2;
    private static final byte YUN = 3;
    private static final byte ZWEI = 4;
    private static final byte DAWN = 5;
    private static final byte SHI = 6;
    private static final byte LOVE = 7;
    private static final byte LIU = 8;
    private static final byte PURPLE_TEAR = 9;
    private static final byte HANA = 10;
    private static final byte BLUE_REVERB = 11;

    public final int screamHits = 2;
    public final int screamDebuff = calcAscensionSpecial(1);
    public final int NUM_VOIDS = calcAscensionSpecial(3);
    public final int INVINCIBLE = 200;

    public final int yunDazes = calcAscensionSpecial(3);
    public final int yunWounds = calcAscensionSpecial(2);

    public final int zweiMetallicize = calcAscensionTankiness(10);
    public final int zweiArmor = calcAscensionTankiness(20);

    public final int dawnBurns = calcAscensionSpecial(2);
    public final int dawnProtection = calcAscensionSpecial(4);

    public final int shiWeak = calcAscensionSpecial(3);
    public final int shiFrail = calcAscensionSpecial(3);

    public final int loveRegen = calcAscensionSpecial(10);
    public final int loveSlimed = calcAscensionSpecial(4);

    public final int liuStrength = calcAscensionSpecial(4);
    public final int liuVulnerable = calcAscensionSpecial(2);

    public final int purpleIntangible = calcAscensionSpecial(1);
    public final int purpleMinions = calcAscensionSpecial(1);

    public final int hanaRegret = calcAscensionSpecial(2);
    public final int hanaHpLoss = calcAscensionSpecial(20);

    private final ArrayList<Byte> memories = new ArrayList<>();

    public BlackSilence4() {
        this(0.0f, 0.0f);
    }

    public BlackSilence4(final float x, final float y) {
        super(NAME, ID, 1300, 0.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BlackSilence1/Spriter/BlackSilence1.scml"));

        numAdditionalMoves = 1;
        maxAdditionalMoves = 99;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }

        this.setHp(calcAscensionTankiness(this.maxHealth));
        this.type = EnemyType.BOSS;

        addMove(AGONY, Intent.ATTACK, calcAscensionDamage(38));
        addMove(SCREAM, Intent.ATTACK_DEBUFF, calcAscensionDamage(14), screamHits, true);
        addMove(VOID, Intent.STRONG_DEBUFF);
        addMove(YUN, Intent.UNKNOWN);
        addMove(ZWEI, Intent.UNKNOWN);
        addMove(DAWN, Intent.UNKNOWN);
        addMove(SHI, Intent.UNKNOWN);
        addMove(LOVE, Intent.UNKNOWN);
        addMove(LIU, Intent.UNKNOWN);
        addMove(PURPLE_TEAR, Intent.UNKNOWN);
        addMove(HANA, Intent.UNKNOWN);
        addMove(BLUE_REVERB, Intent.UNKNOWN);
        populateMemories();

        cardList.add(new Agony(this));
        cardList.add(new Scream(this));
        cardList.add(new Void(this));
        cardList.add(new Yun(this));
        cardList.add(new Zwei(this));
        cardList.add(new Dawn(this));
        cardList.add(new Shi(this));
        cardList.add(new Love(this));
        cardList.add(new Liu(this));
        cardList.add(new Purple(this));
        cardList.add(new Hana(this));
        cardList.add(new Blue(this));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    public void increaseNumIntents() {
        numAdditionalMoves++;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("GoneAngels");
        applyToTarget(this, this, new Scars(this));
        applyToTarget(this, this, new InvinciblePower(this, INVINCIBLE));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case AGONY: {
                dmg(target, info);
                resetIdle();
                break;
            }
            case SCREAM: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                applyToTarget(target, this, new WeakPower(target, screamDebuff, true));
                applyToTarget(target, this, new FrailPower(target, screamDebuff, true));
                break;
            }
            case VOID: {
                intoDiscardMo(new VoidCard(), NUM_VOIDS, this);
                resetIdle(1.0f);
                break;
            }
            case YUN: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Yun1(this));
                options.add(new Yun2(this));
                atb(new ChooseOneAction(options));
                break;
            }
            case ZWEI: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Zwei1(this));
                options.add(new Zwei2(this));
                atb(new ChooseOneAction(options));
                break;
            }
            case DAWN: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Dawn1(this));
                options.add(new Dawn2(this));
                atb(new ChooseOneAction(options));
                break;
            }
            case SHI: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Shi1(this));
                options.add(new Shi2(this));
                atb(new ChooseOneAction(options));
                break;
            }
            case LOVE: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Love1(this));
                options.add(new Love2(this));
                atb(new ChooseOneAction(options));
                break;
            }
            case LIU: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Liu1(this));
                options.add(new Liu2(this));
                atb(new ChooseOneAction(options));
                break;
            }
            case PURPLE_TEAR: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Purple1(this));
                options.add(new Purple2(this));
                atb(new ChooseOneAction(options));
                break;
            }
            case HANA: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Hana1(this));
                options.add(new Hana2(this));
                atb(new ChooseOneAction(options));
                break;
            }
            case BLUE_REVERB: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Blue1(this));
                options.add(new Blue2(this));
                atb(new ChooseOneAction(options));
                break;
            }
        }
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
            takeCustomTurn(additionalMove, adp());
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    additionalIntent.usePrimaryIntentsColor = true;
                    this.isDone = true;
                }
            });
        }
        if (memories.isEmpty()) {
            populateMemories();
        } else {
            memories.remove(0);
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(AGONY)) {
            possibilities.add(AGONY);
        }
        if (!this.lastMove(SCREAM) && !this.lastMoveBefore(SCREAM)) {
            possibilities.add(SCREAM);
        }
        if (!this.lastMove(VOID) && !this.lastMoveBefore(VOID)) {
            possibilities.add(VOID);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (whichMove == numAdditionalMoves - 1) { //the last intent handles memories
            byte move = memories.get(0);
            setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(AGONY, moveHistory)) {
                possibilities.add(AGONY);
            }
            if (!this.lastMove(SCREAM, moveHistory) && !this.lastMoveBefore(SCREAM, moveHistory)) {
                possibilities.add(SCREAM);
            }
            if (!this.lastMove(VOID, moveHistory) && !this.lastMoveBefore(VOID, moveHistory)) {
                possibilities.add(VOID);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
        }
    }

    private void populateMemories() {
        memories.add(YUN);
        memories.add(ZWEI);
        memories.add(DAWN);
        memories.add(SHI);
        memories.add(LOVE);
        memories.add(LIU);
        memories.add(PURPLE_TEAR);
        memories.add(HANA);
        memories.add(BLUE_REVERB);
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof Puppet) {
                atb(new SuicideAction(mo));
            }
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "RolandAxe", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "RolandAxe", enemy, this);
    }

    private void gun1Animation(AbstractCreature enemy) {
        animationAction("Gun1", "RolandRevolver", enemy, this);
    }

    private void gun2Animation(AbstractCreature enemy) {
        animationAction("Gun2", "RolandRevolver", enemy, this);
    }

    private void gun3Animation(AbstractCreature enemy) {
        animationAction("Gun3", "RolandShotgun", enemy, this);
    }

    private void mook1Animation(AbstractCreature enemy) {
        animationAction("Mook1", "RolandLongSwordStart", enemy, this);
    }

    private void mook2Animation(AbstractCreature enemy) {
        animationAction("Mook2", "RolandLongSwordAtk", enemy, this);
        animationAction("Mook2", "RolandLongSwordFin", enemy, this);
    }

    private void claw1Animation(AbstractCreature enemy) {
        animationAction("Claw1", "SwordStab", enemy, this);
    }

    private void claw2Animation(AbstractCreature enemy) {
        animationAction("Claw2", "SwordStab", enemy, this);
    }

    private void knifeAnimation(AbstractCreature enemy) {
        animationAction("Knife", "RolandShortSword", enemy, this);
    }

    private void club1Animation(AbstractCreature enemy) {
        animationAction("Club1", "BluntVert", enemy, this);
    }

    private void club2Animation(AbstractCreature enemy) {
        animationAction("Club2", "BluntVert", enemy, this);
    }

    private void wheelsAnimation(AbstractCreature enemy) {
        animationAction("Wheels", "RolandGreatSword", enemy, this);
    }

    private void sword1Animation(AbstractCreature enemy) {
        animationAction("Sword1", "RolandDuralandalDown", enemy, this);
    }

    private void sword2Animation(AbstractCreature enemy) {
        animationAction("Sword2", "RolandDuralandalUp", enemy, this);
    }

    private void sword3Animation(AbstractCreature enemy) {
        animationAction("Sword3", "RolandDuralandalStrong", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "RolandDualSword", enemy, this);
    }

}
