package ruina.monsters.blackSilence.blackSilence3;

import basemod.ReflectionHacks;
import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.blackSilence.blackSilence3.angelicaCards.AllasWorkshop;
import ruina.monsters.blackSilence.blackSilence3.angelicaCards.AshenBond;
import ruina.monsters.blackSilence.blackSilence3.angelicaCards.AtelierLogic;
import ruina.monsters.blackSilence.blackSilence3.angelicaCards.WaltzInWhite;
import ruina.monsters.blackSilence.blackSilence3.angelicaCards.ZelkovaWorkshop;
import ruina.powers.SoulLink;
import ruina.powers.WhiteNoise;
import ruina.vfx.FlexibleWrathParticleEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Angelica extends AbstractCardMonster {

    public static final String ID = RuinaMod.makeID(Angelica.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public AbstractCard bond;

    private static final byte ZELKOVA = 0;
    private static final byte ALLAS = 1; // sword img
    private static final byte ATELIER = 2;
    private static final byte WALTZ = 3;
    private static final byte ASHENBOND = 4;
    private static final byte NONE = 5;
    private static final byte SOUL_LINK_REVIVAL = 6;

    public final int zelkovaDamage = calcAscensionDamage(8);
    public final int zelkovaHits = 2;
    public final int zelkovaBlock = calcAscensionTankiness(24);
    public final int allasDamage = calcAscensionDamage(19);
    public final int allasDebuff = calcAscensionSpecial(2);
    public final int atelierDamage = calcAscensionDamage(6);
    public final int atelierHits = 3;
    public final int waltzDamage = calcAscensionDamage(8);
    public final int waltzHits = 3;
    public final int bondStrength = calcAscensionSpecial(2);
    public final int bondBlock = calcAscensionTankiness(20);
    public final int REVIVE_PERCENT = calcAscensionSpecial(30);
    private BlackSilence3 roland;
    private static final byte TURNS_UNTIL_WALTZ = 3;
    private int turn = TURNS_UNTIL_WALTZ;

    private float particleTimer;

    public Angelica() {
        this(70.0f, 0.0f);
    }

    public Angelica(final float x, final float y) {
        super(NAME, ID, 600, 0.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Angelica/Spriter/Angelica.scml"));
        this.setHp(calcAscensionTankiness(600));
        this.type = EnemyType.BOSS;
        addMove(ZELKOVA, Intent.ATTACK_DEFEND, zelkovaDamage, zelkovaHits, true);
        addMove(ALLAS, Intent.ATTACK_DEBUFF, allasDamage);
        addMove(ATELIER, Intent.ATTACK, atelierDamage, atelierHits, true);
        addMove(WALTZ, Intent.ATTACK, waltzDamage, waltzHits, true);
        addMove(ASHENBOND, Intent.DEFEND_BUFF);
        cardList.add(new ZelkovaWorkshop(this));
        cardList.add(new AllasWorkshop(this));
        cardList.add(new AtelierLogic(this));
        AbstractCard waltz = new WaltzInWhite(this);
        AbstractCard ashenBond = new AshenBond(this);
        if (AbstractDungeon.ascensionLevel >= 19) {
            waltz.upgrade();
            ashenBond.upgrade();
        }
        cardList.add(waltz);
        cardList.add(ashenBond);
        bond = ashenBond.makeStatEquivalentCopy();
    }

    public void usePreBattleAction() {
        applyToTarget(this, this, new WhiteNoise(this));
        applyToTarget(this, this, new SoulLink(this, REVIVE_PERCENT));
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof BlackSilence3) {
                roland = (BlackSilence3) mo;
            }
        }
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
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
            info.output = this.getIntentDmg();
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
            case ZELKOVA:
                block(this, zelkovaBlock);
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        club1Animation(target);
                    } else {
                        club2Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            case ALLAS: {
                wheelsAnimation(target);
                dmg(target, info);
                resetIdle();
                if (isRolandAttacking()) {
                    applyToTarget(target, this, new VulnerablePower(target, allasDebuff, true));
                }
                break;
            }
            case ATELIER:
                for (int i = 0; i < multiplier; i++) {
                    gunAnimation(target);
                    dmg(target, info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            case WALTZ: {
                gunAnimation(target);
                dmg(target, info);
                resetIdle();
                if (!roland.isDeadOrEscaped()) {
                    roland.sword1Animation(target);
                    dmg(target, info);
                    roland.resetIdle();
                } else {
                    club1Animation(target);
                    dmg(target, info);
                    resetIdle();
                }
                wheelsAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case ASHENBOND:
                guardAnimation();
                int effectMultiplier = 1;
                if (AbstractDungeon.ascensionLevel >= 19 && !isRolandAttacking()) {
                    effectMultiplier = 2;
                }
                for (AbstractMonster mo : monsterList()) {
                    if (!mo.isDeadOrEscaped()) {
                        block(mo, bondBlock * effectMultiplier);
                        applyToTarget(mo, this, new StrengthPower(this, bondStrength * effectMultiplier));
                    }
                }
                resetIdle();
                break;
            case SOUL_LINK_REVIVAL:
                atb(new HealAction(this, this, (int)(this.maxHealth * ((float)REVIVE_PERCENT / 100))));
                halfDead = false;
                break;
        }
        atb(new RollMoveAction(this));
    }

    private boolean isRolandAttacking() {
        return roland.getIntentBaseDmg() >= 0;
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
            if (!this.lastMove(ZELKOVA) && !this.lastMoveBefore(ZELKOVA)) {
                possibilities.add(ZELKOVA);
            }
            if (!this.lastMove(ALLAS) && !this.lastMoveBefore(ALLAS)) {
                possibilities.add(ALLAS);
            }
            if (!this.lastMove(ATELIER) && !this.lastMoveBefore(ATELIER)) {
                possibilities.add(ATELIER);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
        }
    }

    public void setBondIntent() {
        setMoveShortcut(ASHENBOND, MOVES[ASHENBOND], cardList.get(ASHENBOND).makeStatEquivalentCopy());
        createIntent();
    }

    public void damage(DamageInfo info) {
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

            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power instanceof StrengthPower) && !(power instanceof GainStrengthPower) && !(power instanceof ruina.powers.WhiteNoise) && !(power instanceof SoulLink)) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }

            boolean allDead = true;
            for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                if (m.id.equals(BlackSilence3.ID) && !m.halfDead) {
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
        if (!(AbstractDungeon.getCurrRoom()).cannotLose) super.die();
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        this.particleTimer -= Gdx.graphics.getDeltaTime();
        if (this.particleTimer < 0.0F) {
            this.particleTimer = 0.04F;
            AbstractDungeon.effectsQueue.add(new FlexibleWrathParticleEffect(this, Color.WHITE.cpy()));
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        if (this.nextMove == WALTZ && AbstractDungeon.ascensionLevel >= 19) {
            DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            info.applyPowers(this, adp());
            if (isRolandAttacking()) {
                info.output *= 2;
            }
            if (this.hasPower(BackAttackPower.POWER_ID)) {
                info.output = (int)((float)info.output * 1.5F);
            }
            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
            Texture attackImg = getAttackIntent(info.output * waltzHits);
            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentImg", attackImg);
            updateCard();
        }
    }

    public void guardAnimation() {
        animationAction("Guard", null, this);
    }

    public void gunAnimation(AbstractCreature enemy) {
        animationAction("Fire", "RolandShotgun", enemy, this);
    }

    public void club1Animation(AbstractCreature enemy) {
        animationAction("Hit", "BluntVert", enemy, this);
    }

    public void club2Animation(AbstractCreature enemy) {
        animationAction("Slash", "BluntVert", enemy, this);
    }

    public void wheelsAnimation(AbstractCreature enemy) {
        animationAction("GreatSword", "RolandGreatSword", enemy, this);
    }

}
