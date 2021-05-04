package ruina.monsters.blackSilence.blackSilence3;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.SurroundedPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.blackSilence.blackSilence3.rolandCards.BlindFury;
import ruina.monsters.blackSilence.blackSilence3.rolandCards.DarkBond;
import ruina.monsters.blackSilence.blackSilence3.rolandCards.UnitedWorkshop;
import ruina.monsters.blackSilence.blackSilence3.rolandCards.UnstableLoneliness;
import ruina.monsters.blackSilence.blackSilence3.rolandCards.WaltzInBlack;
import ruina.powers.Paralysis;
import ruina.powers.SoulLink;

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

    private static final byte UNITED_WORKSHOP = 0;
    private static final byte LONELINESS = 1;
    private static final byte FURY = 2;
    private static final byte WALTZ = 3;
    private static final byte DARKBOND = 4;
    private static final byte NONE = 5;
    private static final byte SOUL_LINK_REVIVAL = 6;

    public final int unitedDamage = calcAscensionDamage(8);
    public final int unitedHits = 2;
    public final int unitedStrength = calcAscensionSpecial(3);
    public final int lonelyDamage = calcAscensionDamage(22);
    public final int lonelyDebuff = calcAscensionSpecial(2);
    public final int furyDamage = calcAscensionDamage(20);
    public final int furyDebuff = calcAscensionSpecial(2);
    public final int waltzDamage = calcAscensionDamage(8);
    public final int waltzHits = 3;
    public final int bondBlock = calcAscensionTankiness(20);
    public final int bondVoid = calcAscensionSpecial(2);
    public final int REVIVE_PERCENT = calcAscensionSpecial(30);
    private static final byte TURNS_UNTIL_WALTZ = 3;
    private int turn = TURNS_UNTIL_WALTZ;
    private Angelica angelica;

    public BlackSilence3() {
        this(-1000.0f, 0f);
    }

    public BlackSilence3(final float x, final float y) {
        super(NAME, ID, 600, 0.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BlackSilence3/Spriter/BlackSilence3.scml"));
        this.animation.setFlip(true, false);
        this.setHp(calcAscensionTankiness(this.maxHealth));
        this.type = EnemyType.BOSS;

        addMove(UNITED_WORKSHOP, Intent.ATTACK_BUFF, unitedDamage, unitedHits, true);
        addMove(LONELINESS, Intent.ATTACK_DEBUFF, lonelyDamage);
        addMove(FURY, Intent.ATTACK_DEBUFF, furyDamage);
        addMove(WALTZ, Intent.ATTACK, waltzDamage, waltzHits, true);
        addMove(DARKBOND, Intent.DEFEND_DEBUFF);

        cardList.add(new UnitedWorkshop(this));
        cardList.add(new UnstableLoneliness(this));
        cardList.add(new BlindFury(this));
        cardList.add(new WaltzInBlack(this));
        cardList.add(new DarkBond(this));
        bond = new DarkBond(this);
    }

    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Roland3");
        (AbstractDungeon.getCurrRoom()).cannotLose = true;
        applyToTarget(this, this, new ruina.powers.BlackSilence3(this));
        applyToTarget(this, this, new SoulLink(this, REVIVE_PERCENT));
        AbstractDungeon.player.drawX += 480.0F * Settings.scale;
        AbstractDungeon.player.dialogX += 480.0F * Settings.scale;
        applyToTarget(adp(), this, new SurroundedPower(adp()));
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof Angelica) {
                angelica = (Angelica) mo;
            }
        }
    }

    @Override
    public void takeTurn() {
        DamageInfo info;
        int multiplier = 0;
        if (moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }
        AbstractCreature target = adp();
        if (info.base > -1) {
            info.output = this.getIntentDmg(); //fuck back attack
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (turn <= 0) {
                    turn = TURNS_UNTIL_WALTZ;
                } else {
                    turn -= 1;
                }
                isDone = true;
            }
        });
        switch (this.nextMove) {
            case UNITED_WORKSHOP:
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                if (isAngelicaAttacking()) {
                    applyToTarget(this, this, new StrengthPower(this, unitedStrength));
                }
                break;
            case LONELINESS: {
                sword1Animation(target);
                dmg(target, info);
                resetIdle();
                if (!isAngelicaAttacking()) {
                    applyToTarget(adp(), this, new WeakPower(adp(), lonelyDebuff, true));
                    applyToTarget(adp(), this, new FrailPower(adp(), lonelyDebuff, true));
                }
                break;
            }
            case FURY:
                sword2Animation(target);
                dmg(target, info);
                applyToTarget(adp(), this, new Paralysis(adp(), furyDebuff));
                resetIdle();
                break;
            case WALTZ: {
                pierceAnimation(target);
                dmg(target, info);
                resetIdle();
                if (!angelica.isDeadOrEscaped()) {
                    angelica.gunAnimation(target);
                    dmg(target, info);
                    angelica.resetIdle();
                } else {
                    sword1Animation(target);
                    dmg(target, info);
                    resetIdle();
                }
                slashAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case DARKBOND:
                guardAnimation();
                for (AbstractMonster mo : monsterList()) {
                    if (!mo.isDeadOrEscaped()) {
                        block(mo, bondBlock);
                    }
                }
                intoDrawMo(new VoidCard(), bondVoid, this);
                resetIdle();
                break;
            case SOUL_LINK_REVIVAL:
                atb(new HealAction(this, this, (int) (this.maxHealth * ((float) REVIVE_PERCENT / 100))));
                halfDead = false;
                break;
        }
        atb(new RollMoveAction(this));
    }

    private boolean isAngelicaAttacking() {
        return angelica.getIntentBaseDmg() >= 0;
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        tips.add(new CardPowerTip(bond.makeStatEquivalentCopy()));
    }

    @Override
    protected void getMove(final int num) {
        if (turn <= 0) {
            setMoveShortcut(WALTZ, MOVES[WALTZ], cardList.get(WALTZ).makeStatEquivalentCopy());
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(UNITED_WORKSHOP) && !this.lastMoveBefore(UNITED_WORKSHOP)) {
                possibilities.add(UNITED_WORKSHOP);
            }
            if (!this.lastMove(LONELINESS) && !this.lastMoveBefore(LONELINESS)) {
                possibilities.add(LONELINESS);
            }
            if (!this.lastMove(FURY) && !this.lastMoveBefore(FURY)) {
                possibilities.add(FURY);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
        }
    }

    public void setBondIntent() {
        setMoveShortcut(DARKBOND, MOVES[DARKBOND], cardList.get(DARKBOND).makeStatEquivalentCopy());
        createIntent();
    }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            for (AbstractPower p : this.powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power instanceof StrengthPower) && !(power instanceof GainStrengthPower) && !(power instanceof ruina.powers.BlackSilence3) && !(power instanceof SoulLink)) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }

            boolean allDead = true;
            for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                if (m instanceof Angelica && !m.halfDead) {
                    allDead = false;
                }
            }

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
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    m.die();
                }
            }
        }
    }

    public void die() {
        if (!(AbstractDungeon.getCurrRoom()).cannotLose) {
            super.die();
        }
    }

    public void guardAnimation() {
        animationAction("Guard", null, this);
    }

    public void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "RolandAxe", enemy, this);
    }

    public void sword1Animation(AbstractCreature enemy) {
        animationAction("SwordDown", "RolandDuralandalDown", enemy, this);
    }

    public void sword2Animation(AbstractCreature enemy) {
        animationAction("SwordUp", "RolandDuralandalUp", enemy, this);
    }

    public void slashAnimation(AbstractCreature enemy) {
        animationAction("Hit", "RolandDualSword", enemy, this);
    }
}
