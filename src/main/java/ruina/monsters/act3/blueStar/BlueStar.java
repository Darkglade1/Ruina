package ruina.monsters.act3.blueStar;

import actlikeit.dungeons.CustomDungeon;
import basemod.animations.AbstractAnimation;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class BlueStar extends AbstractRuinaMonster
{
    public static final String ID = makeID(BlueStar.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte RISING_STAR = 0;
    private static final byte STARRY_SKY = 1;
    private static final byte SOUND_OF_STAR = 2;

    private final int BLOCK = calcAscensionTankiness(14);
    private final int STRENGTH = calcAscensionSpecial(3);

    private static final int HP_LOSS = 100;

    public static final String POWER_ID = makeID("Worshippers");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public AbstractMonster[] minions = new AbstractMonster[2];
    private boolean canPlayMusic = true;
    private final AbstractAnimation star;

    public BlueStar() {
        this(150.0f, 0.0f);
    }

    public BlueStar(final float x, final float y) {
        super(NAME, ID, 110, 10.0F, 0, 200.0f, 305.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BlueStar/Spriter/BlueStar.scml"));
        this.star = new BetterSpriterAnimation(makeMonsterPath("BlueStar/Star/Star.scml"));
        this.type = EnemyType.ELITE;
        setHp(calcAscensionTankiness(500));
        addMove(RISING_STAR, Intent.DEFEND);
        addMove(STARRY_SKY, Intent.BUFF);
        addMove(SOUND_OF_STAR, Intent.ATTACK, calcAscensionDamage(30));
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning3");
        AbstractPower power = new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, HP_LOSS) {
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }

            @Override
            public void atEndOfRound() {
                Summon();
            }
        };
        applyToTarget(this, this, power);
        Summon();
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if (this.firstMove) {
            firstMove = false;
        }

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case RISING_STAR: {
                for (AbstractMonster mo : monsterList()) {
                    block(mo, BLOCK);
                }
                break;
            }
            case STARRY_SKY: {
                for (AbstractMonster mo : monsterList()) {
                    applyToTarget(mo, this, new StrengthPower(mo, STRENGTH));
                }
                break;
            }
            case SOUND_OF_STAR: {
                playSound("BlueStarCharge");
                atb(new VFXAction(new WaitEffect(), 0.2f));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        playSound("BlueStarAtk");
                        ((BetterSpriterAnimation)star).myPlayer.setAnimation("Attack");
                        this.isDone = true;
                    }
                });
                dmg(adp(), info);
                atb(new VFXAction(new WaitEffect(), 0.5f));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        ((BetterSpriterAnimation)star).myPlayer.setAnimation("Idle");
                        this.isDone = true;
                    }
                });
                break;
            }
        }
        atb(new RollMoveAction(this));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                canPlayMusic = true;
                this.isDone = true;
            }
        });
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(RISING_STAR)) {
            setMoveShortcut(STARRY_SKY, MOVES[STARRY_SKY]);
        } else if (this.lastMove(STARRY_SKY)) {
            setMoveShortcut(SOUND_OF_STAR, MOVES[SOUND_OF_STAR]);
        } else {
            setMoveShortcut(RISING_STAR, MOVES[RISING_STAR]);
        }
        if (canPlayMusic) {
            canPlayMusic = false;
            if (nextMove == SOUND_OF_STAR) {

            }
        }
    }

    public void Summon() {
        float xPos_Farthest_L = -450.0F;
        float xPos_Middle_L = -150F;

        for (int i = 0; i < minions.length; i++) {
            if (minions[i] == null) {
                AbstractMonster minion;
                if (i == 0) {
                    minion = new Worshipper(xPos_Farthest_L, 0.0f, this);
                } else {
                    minion = new Worshipper(xPos_Middle_L, 0.0f, this);
                }
                atb(new SpawnMonsterAction(minion, true));
                atb(new UsePreBattleActionAction(minion));
                if (!firstMove) {
                    minion.rollMove();
                    minion.createIntent();
                }
                minions[i] = minion;
            }
        }
    }

    public void onMinionDeath() {
        atb(new LoseHPAction(this, this, HP_LOSS));
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof Worshipper) {
                ((Worshipper) mo).triggerMartyr = false;
                atb(new SuicideAction(mo));
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!isDead) {
            sb.setColor(Color.WHITE);
            star.renderSprite(sb, (float) Settings.WIDTH / 2, (float) Settings.HEIGHT / 2);
        }
        super.render(sb);
    }

}