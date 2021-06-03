package ruina.monsters.act1.laetitia;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.cards.Gift;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.LonelyIsSad;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Laetitia extends AbstractRuinaMonster {
    public static final String ID = makeID(Laetitia.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte GIFT = 0;
    private static final byte FUN = 1;

    private final int giftDamage = calcAscensionDamage(4);
    private final int giftGifts = 1;

    private final int funDamage = calcAscensionDamage(2);
    private final int funHits = 2;

    public Laetitia() {
        this(140.0f, 0.0f);
    }

    public Laetitia(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 200.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Laetitia/Spriter/Laetitia.scml"));
        this.type = EnemyType.ELITE;
        setHp(calcAscensionTankiness(110));
        addMove(GIFT, Intent.ATTACK_DEBUFF, giftDamage);
        addMove(FUN, Intent.ATTACK, funDamage, funHits, true);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning1");
        atb(new ApplyPowerAction(this, this, new LonelyIsSad(this)));
        Summon();
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;
        if (info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case GIFT: {
                attackAnimation(adp());
                dmg(adp(), info);
                atb(new MakeTempCardInHandAction(new Gift(), giftGifts));
                resetIdle();
                break;
            }
            case FUN: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(adp());
                    dmg(adp(), info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastTwoMoves(GIFT)) {
            setMoveShortcut(FUN, MOVES[FUN]);
        } else {
            setMoveShortcut(GIFT, MOVES[GIFT]);
        }
    }


    public void Summon() {
        float xPos_Middle_L = -85;
        float xPos_Short_L = -330f;
        AbstractMonster giftFriend1 = new GiftFriend(xPos_Middle_L, 0.0f, this);
        atb(new SpawnMonsterAction(giftFriend1, true));
        atb(new UsePreBattleActionAction(giftFriend1));
        giftFriend1.rollMove();
        giftFriend1.createIntent();
        AbstractMonster giftFriend2 = new GiftFriend(xPos_Short_L, 0.0f, this);
        atb(new SpawnMonsterAction(giftFriend2, true));
        atb(new UsePreBattleActionAction(giftFriend2));
        giftFriend2.rollMove();
        giftFriend2.createIntent();
    }

    public void onMonsterDeath() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractPower p = getPower(LonelyIsSad.POWER_ID);
                if (p != null && monsterList().size() == 1) {
                    p.flash();
                    ((LonelyIsSad) p).doubleDamage = true;
                    p.updateDescription();
                }
                isDone = true;
            }
        });
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof GiftFriend || mo instanceof WitchFriend) {
                atb(new SuicideAction(mo));
            }
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "LaetitiaAtk", enemy, this);
    }

}