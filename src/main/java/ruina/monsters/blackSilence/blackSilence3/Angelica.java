package ruina.monsters.blackSilence.blackSilence3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.blackSilence.blackSilence3.angelicaCards.*;
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
    public AbstractCard waltz;

    public boolean rolandAttackedThisTurn = false;

    private static final byte ZELKOVA = 0;
    private static final byte ALLAS = 1; // sword img
    private static final byte ATELIER = 2;
    private static final byte WALTZ = 3;
    private static final byte ASHENBOND = 4;
    private static final byte NONE = 5;
    private static final byte SOUL_LINK_REVIVAL = 6;

    public final int zelkovaDamage = calcAscensionDamage(8);
    public final int zelkovaHits = 2;
    public final int allasDamage = calcAscensionDamage(20);
    public final int allasDebuff = 1;
    public final int atelierDamage = calcAscensionDamage(6);
    public final int atelierHits = 3;
    public final int waltzDamage = calcAscensionDamage(10);
    public final int waltzHits = 3;
    public final int bondStrength = calcAscensionSpecial(3);
    private BlackSilence3 roland;
    private static final byte TURNS_UNTIL_WALTZ = 3;
    private int turn = TURNS_UNTIL_WALTZ;

    private float particleTimer;
    private float particleTimer2;

    public Angelica() {
        this(-1000.0f, 0.0f);
    }

    public Angelica(final float x, final float y) {
        super(NAME, ID, 450, 0.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Angelica/Spriter/Angelica.scml"));
        this.setHp(calcAscensionTankiness(this.maxHealth));
        this.type = EnemyType.BOSS;
        addMove(ZELKOVA, Intent.ATTACK, zelkovaDamage, zelkovaHits, true);
        addMove(ALLAS, Intent.ATTACK_DEBUFF, allasDamage);
        addMove(ATELIER, Intent.ATTACK, atelierDamage, atelierHits, true);
        addMove(WALTZ, Intent.ATTACK, waltzDamage, waltzHits, true);
        addMove(ASHENBOND, Intent.BUFF);
        cardList.add(new ZelkovaWorkshop(this));
        cardList.add(new AllasWorkshop(this));
        cardList.add(new AtelierLogic(this));
        cardList.add(new WaltzInWhite(this));
        cardList.add(new AshenBond(this));
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
                else if(nextMove != NONE) {turn -= 1; }
                isDone = true;
            }
        });
        switch (this.nextMove) {
            case ZELKOVA:
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info);
                }
                break;
            case ALLAS: {
                dmg(target, info);
                applyToTarget(target, this, new VulnerablePower(target, allasDebuff, true));
                break;
            }
            case ATELIER:
            case WALTZ: {
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info);
                }
                break;
            }
            case ASHENBOND:
                applyToTarget(roland, this, new StrengthPower(roland, bondStrength));
                break;
            case SOUL_LINK_REVIVAL:
                atb(new HealAction(this, this, this.maxHealth));
                break;
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractPower power = Angelica.this.getPower(ruina.powers.WhiteNoise.POWER_ID);
                if(power != null && power.amount == -1){
                    Angelica.this.setEmptyMove();
                    createIntent();
                }
                else { att(new RollMoveAction(Angelica.this)); }
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
        if (turn == 0) {
            setMoveShortcut(WALTZ, MOVES[WALTZ], cardList.get(WALTZ).makeStatEquivalentCopy());
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(ZELKOVA)) {
                possibilities.add(ZELKOVA);
            }
            if (!this.lastTwoMoves(ALLAS)) {
                possibilities.add(ALLAS);
            }
            if (!this.lastTwoMoves(ATELIER)) {
                possibilities.add(ATELIER);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
        }
    }

    public void usePreBattleAction() {
        applyToTarget(this, this, new WhiteNoise(this));
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof BlackSilence3) {
                roland = (BlackSilence3) mo;
            }
        }
    }

    public void setEmptyMove() { atb(new SetMoveAction(this, NONE, Intent.NONE)); }
    public void setBondIntent(){ setMoveShortcut(ASHENBOND, MOVES[ASHENBOND]); }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            for (AbstractPower p : this.powers) { p.onDeath(); }
            for (AbstractRelic r : AbstractDungeon.player.relics) { r.onMonsterDeath(this); }
            this.powers.clear();
            boolean allDead = true;
            for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                if (m.id.equals(BlackSilence3.ID) && !m.halfDead) { allDead = false; }
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
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) { m.die(); }
            }
        }
    }

    public void die() { if (!(AbstractDungeon.getCurrRoom()).cannotLose) super.die(); }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        this.particleTimer -= Gdx.graphics.getDeltaTime();
        if (this.particleTimer < 0.0F) {
            this.particleTimer = 0.04F;
            AbstractDungeon.effectsQueue.add(new FlexibleWrathParticleEffect(this, Color.WHITE.cpy()));
        }

        /*
        this.particleTimer2 -= Gdx.graphics.getDeltaTime();
        if (this.particleTimer2 < 0.0F) {
            this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
            AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect("BS3_LIGHT", this));
        }

         */
    }

}
