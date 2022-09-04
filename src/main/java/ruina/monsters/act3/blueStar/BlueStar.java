package ruina.monsters.act3.blueStar;

import actlikeit.dungeons.CustomDungeon;
import basemod.animations.AbstractAnimation;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.CenterOfAttention;
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
    private static final byte WORSHIPPERS = 3;

    private final int BLOCK = calcAscensionTankiness(13);
    private final int STRENGTH = calcAscensionSpecial(4);
    private final int VULNERABLE = calcAscensionSpecial(1);

    public AbstractMonster[] minions = new AbstractMonster[2];
    private final AbstractAnimation star;
    private int moveCounter = 0;

    public BlueStar() {
        this(150.0f, 0.0f);
    }

    public BlueStar(final float x, final float y) {
        super(NAME, ID, 200, 0.0F, 0, 250.0f, 345.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BlueStar/Spriter/BlueStar.scml"));
        this.star = new BetterSpriterAnimation(makeMonsterPath("BlueStar/Star/Star.scml"));
        this.type = EnemyType.ELITE;
        setHp(calcAscensionTankiness(200));
        addMove(RISING_STAR, Intent.DEFEND_DEBUFF);
        addMove(STARRY_SKY, Intent.BUFF);
        addMove(SOUND_OF_STAR, Intent.ATTACK, calcAscensionDamage(28));
        addMove(WORSHIPPERS, Intent.UNKNOWN);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning3");
        applyToTarget(this, this, new CenterOfAttention(this));
        Summon();
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        if (firstMove) {
            firstMove = false;
        }

        switch (this.nextMove) {
            case RISING_STAR: {
                for (AbstractMonster mo : monsterList()) {
                    block(mo, BLOCK);
                }
                applyToTarget(adp(), this, new VulnerablePower(adp(), VULNERABLE, true));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        moveCounter = 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case STARRY_SKY: {
                for (AbstractMonster mo : monsterList()) {
                    applyToTarget(mo, this, new StrengthPower(mo, STRENGTH));
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        moveCounter = 2;
                        this.isDone = true;
                    }
                });
                break;
            }
            case SOUND_OF_STAR: {
                playSound("BlueStarCharge");
                atb(new VFXAction(new WaitEffect(), 0.5f));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        playSound("BlueStarAtk");
                        ((BetterSpriterAnimation)star).myPlayer.setAnimation("Attack");
                        this.isDone = true;
                    }
                });
                dmg(adp(), info);
                atb(new VFXAction(new WaitEffect(), 1.0f));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        ((BetterSpriterAnimation)star).myPlayer.setAnimation("Idle");
                        this.isDone = true;
                    }
                });
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        moveCounter = 0;
                        this.isDone = true;
                    }
                });
                break;
            }
            case WORSHIPPERS: {
                Summon();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (!firstMove && minions[0] == null && minions[1] == null) {
            setMoveShortcut(WORSHIPPERS, MOVES[WORSHIPPERS]);
        } else {
            if (moveCounter == 0) {
                setMoveShortcut(RISING_STAR, MOVES[RISING_STAR]);
            } else if (moveCounter == 1) {
                setMoveShortcut(STARRY_SKY, MOVES[STARRY_SKY]);
            } else {
                setMoveShortcut(SOUND_OF_STAR, MOVES[SOUND_OF_STAR]);
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
                minions[i] = minion;
            }
        }
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
            star.renderSprite(sb, (float) Settings.WIDTH / 2, (float) Settings.HEIGHT / 2 + (150.0F * Settings.scale));
        }
        super.render(sb);
    }

}