package ruina.monsters.eventboss.lulu.monster;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.stances.WrathStance;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.ChooseOneActionButItCanFizzle;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.blackSilence.blackSilence4.ImageOfBygones;
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
import ruina.monsters.eventboss.lulu.cards.CHRBOSS_FlamingBat;
import ruina.monsters.eventboss.lulu.cards.CHRBOSS_PreparedMind;
import ruina.monsters.eventboss.lulu.cards.CHRBOSS_SetAblaze;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Burn;
import ruina.powers.Paralysis;
import ruina.powers.Scars;
import ruina.util.AdditionalIntent;
import ruina.vfx.FlexibleStanceAuraEffect;
import ruina.vfx.FlexibleWrathParticleEffect;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.atb;

public class Lulu extends AbstractCardMonster {
    public static final String ID = RuinaMod.makeID(Lulu.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte PREPARED_MIND = 0;
    private static final byte FLAMING_BAT = 1;
    private static final byte SET_ABLAZE = 2;

    public final int PREPARED_MIND_STRENGTH = 2;
    public final int FLAMING_BAT_DAMAGE = calcAscensionDamage(7);
    public final int FLAMING_BAT_VULNERABLE = 1;
    public final int SET_ABLAZE_DAMAGE = calcAscensionDamage(4);
    public final int SET_ABLAZE_HITS = 3;

    public static final String POWER_ID = makeID("FlamingBat");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int FLAMING_BAT_BURN_AMOUNT = 3;

    public Lulu() {
        this(0.0f, 0.0f);
    }
    public Lulu(final float x, final float y) {
        super(NAME, ID, 120, -15.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("RedMist/Spriter/RedMist.scml"));

        numAdditionalMoves = 0;
        maxAdditionalMoves = 4;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }

        this.setHp(calcAscensionTankiness(this.maxHealth));
        this.type = EnemyType.ELITE;

        addMove(PREPARED_MIND, Intent.BUFF);
        addMove(FLAMING_BAT, Intent.ATTACK_DEBUFF, FLAMING_BAT_DAMAGE);
        addMove(SET_ABLAZE, Intent.ATTACK, SET_ABLAZE_DAMAGE, SET_ABLAZE_HITS, true);

        cardList.add(new CHRBOSS_PreparedMind(this));
        cardList.add(new CHRBOSS_FlamingBat(this));
        cardList.add(new CHRBOSS_SetAblaze(this));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    public void increaseNumIntents() {
        numAdditionalMoves++;
        if (numAdditionalMoves > maxAdditionalMoves) {
            numAdditionalMoves = maxAdditionalMoves;
        }
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, FLAMING_BAT_BURN_AMOUNT) {
            @Override
            public void updateDescription() {
                description = String.format(POWER_DESCRIPTIONS[0], amount);
            }
        });
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        if(info.base > -1) { info.applyPowers(this, target); }
        switch (move.nextMove) {
            case PREPARED_MIND: {
                applyToTarget(this, this, new StrengthPower(this, PREPARED_MIND_STRENGTH));
                break;
            }
            case FLAMING_BAT: {
                dmg(target, info);
                applyToTarget(adp(), this, new VulnerablePower(adp(), FLAMING_BAT_VULNERABLE, true));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(adp().lastDamageTaken > 0){
                            applyToTargetTop(adp(), Lulu.this, new Burn(adp(), FLAMING_BAT_BURN_AMOUNT));
                        }
                        isDone = true;
                    }
                });
                break;
            }
            case SET_ABLAZE: {
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info);
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            if(adp().lastDamageTaken > 0){
                                applyToTargetTop(adp(), Lulu.this, new Burn(adp(), FLAMING_BAT_BURN_AMOUNT));
                            }
                            isDone = true;
                        }
                    });
                }
                firstMove = true;
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
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) { setMoveShortcut(PREPARED_MIND, MOVES[PREPARED_MIND],  cardList.get(PREPARED_MIND).makeStatEquivalentCopy()); }
        else if (this.lastMove(PREPARED_MIND)) { setMoveShortcut(FLAMING_BAT, MOVES[FLAMING_BAT],  cardList.get(FLAMING_BAT).makeStatEquivalentCopy()); }
        else { setMoveShortcut(SET_ABLAZE, MOVES[SET_ABLAZE],  cardList.get(SET_ABLAZE).makeStatEquivalentCopy()); }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
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


}