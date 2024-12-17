package ruina.monsters.uninvitedGuests.normal.puppeteer;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.AllyGainBlockAction;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.uninvitedGuests.normal.puppeteer.chesedCards.BattleCommand;
import ruina.monsters.uninvitedGuests.normal.puppeteer.chesedCards.Concentration;
import ruina.monsters.uninvitedGuests.normal.puppeteer.chesedCards.Disposal;
import ruina.monsters.uninvitedGuests.normal.puppeteer.chesedCards.EnergyShield;
import ruina.powers.act4.FinishingTouch;
import ruina.powers.act4.Mark;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class Chesed extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Chesed.class.getSimpleName());

    private static final byte BATTLEFIELD_COMMAND = 0;
    private static final byte ENERGY_SHIELD = 1;
    private static final byte CONCENTRATE = 2;
    private static final byte DISPOSAL = 3;

    public final int STRENGTH = 2;
    public final int ENERGY_SHIELD_BLOCK = 15;
    public final int CONCENTRATE_BLOCK = 18;
    public final int CONCENTRATE_HITS = 2;
    public final int DISPOSAL_HITS = 2;
    
    private static final float MARK_BOOST = 0.50f;
    private static final int MARK_DURATION = 1;

    private static final float DISPOSAL_HP_THRESHOLD = 0.25f;

    public Puppeteer puppeteer;

    public Chesed() {
        this(0.0f, 0.0f);
    }

    public Chesed(final float x, final float y) {
        super(ID, ID, 150, -5.0F, 0, 230.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Chesed/Spriter/Chesed.scml"));
        this.animation.setFlip(true, false);
        this.setHp(calcAscensionTankiness(150));

        addMove(BATTLEFIELD_COMMAND, Intent.ATTACK_BUFF, 18);
        addMove(ENERGY_SHIELD, Intent.ATTACK_DEFEND, 7);
        addMove(CONCENTRATE, Intent.ATTACK_DEFEND, 11, CONCENTRATE_HITS);
        addMove(DISPOSAL, Intent.ATTACK, 12, DISPOSAL_HITS);

        cardList.add(new BattleCommand(this));
        cardList.add(new EnergyShield(this));
        cardList.add(new Concentration(this));
        cardList.add(new Disposal(this));

        this.icon = TexLoader.getTexture(makeUIPath("ChesedIcon.png"));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Puppeteer) {
                target = puppeteer = (Puppeteer)mo;
            }
        }
        addPower(new FinishingTouch(this, MARK_DURATION, MARK_BOOST));
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        if (firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();
        switch (this.nextMove) {
            case BATTLEFIELD_COMMAND: {
                slashAnimation(target);
                dmg(target, info);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                applyToTarget(adp(), this, new StrengthPower(adp(), STRENGTH));
                resetIdle();
                break;
            }
            case ENERGY_SHIELD: {
                blockAnimation();
                atb(new AllyGainBlockAction(this, this, ENERGY_SHIELD_BLOCK));
                block(adp(), ENERGY_SHIELD_BLOCK);
                waitAnimation();
                pierceAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case CONCENTRATE: {
                atb(new AllyGainBlockAction(this, this, CONCENTRATE_BLOCK));
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case DISPOSAL: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        disposalUp(target);
                        waitAnimation();
                        disposalDown(target);
                    } else {
                        moveAnimation(target);
                        waitAnimation();
                        disposalFinish(target);
                    }
                    addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            info.applyPowers(Chesed.this, puppeteer);
                            if (puppeteer.hasPower(Mark.POWER_ID)) {
                                info.output *= 2;
                            }
                            if (puppeteer.currentHealth <= (int)(puppeteer.maxHealth * DISPOSAL_HP_THRESHOLD)) {
                                info.output *= 2;
                            }
                            this.isDone = true;
                        }
                    });
                    dmg(target, info);
                    resetIdle(1.0f);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (moveHistory.size() >= 3) {
            moveHistory.clear(); //resets the cooldowns after all moves have been used once
        }
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(BATTLEFIELD_COMMAND) && !this.lastMoveBefore(BATTLEFIELD_COMMAND)) {
            possibilities.add(BATTLEFIELD_COMMAND);
        }
        if (!this.lastMove(ENERGY_SHIELD) && !this.lastMoveBefore(ENERGY_SHIELD)) {
            possibilities.add(ENERGY_SHIELD);
        }
        if (!this.lastMove(CONCENTRATE) && !this.lastMoveBefore(CONCENTRATE)) {
            possibilities.add(CONCENTRATE);
        }
        if (possibilities.isEmpty()) {
            possibilities.add(CONCENTRATE);
        }
        byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
        setMoveShortcut(move, cardList.get(move));
    }

    public void checkDisposalCanUse() {
        if (puppeteer.currentHealth <= (int)(puppeteer.maxHealth * DISPOSAL_HP_THRESHOLD)) {
            if (puppeteer.puppet.isDeadOrEscaped() && puppeteer.puppet.nextMove == Puppet.REVIVE) {
                if (puppeteer.hasPower(Mark.POWER_ID)) {
                    att(new TalkAction(this, DIALOG[2]));
                    setMoveShortcut(DISPOSAL, cardList.get(DISPOSAL));
                    createIntent();
                }
            }
        }
    }

    @Override
    public void applyPowers() {
        if (puppeteer.puppet.isDeadOrEscaped()) {
            target = puppeteer;
        } else {
            target = puppeteer.puppet;
        }
        super.applyPowers();
    }

    protected float getAdditionalMultiplier() {
        if (nextMove == DISPOSAL) {
            float multiplier = 1;
            if (puppeteer.hasPower(Mark.POWER_ID)) {
                multiplier *= 2;
            }
            if (puppeteer.currentHealth <= (int)(puppeteer.maxHealth * DISPOSAL_HP_THRESHOLD)) {
                multiplier *= 2;
            }
            return multiplier;
        }
        return -1;
    }

    public void onBossDeath() {
        if (!isDead && !isDying) {
            atb(new TalkAction(this, DIALOG[1]));
            atb(new VFXAction(new WaitEffect(), 1.0F));
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    disappear();
                    this.isDone = true;
                }
            });
        }
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "SwordVert", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "SwordStab", enemy, this);
    }

    private void disposalUp(AbstractCreature enemy) {
        animationAction("Special3", "DisposalUp", enemy, this);
    }

    private void disposalDown(AbstractCreature enemy) {
        animationAction("Special4", "DisposalDown", enemy, this);
    }

    private void disposalFinish(AbstractCreature enemy) {
        animationAction("Special2", "DisposalFinish", enemy, this);
        animationAction("Special2", "DisposalBlood", enemy, this);
    }

    private void moveAnimation(AbstractCreature enemy) {
        animationAction("Move", null, enemy, this);
    }

}