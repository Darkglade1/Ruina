package ruina.monsters.blackSilence.blackSilence3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
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
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class BlackSilence3 extends AbstractCardMonster {

    public static final String ID = RuinaMod.makeID(BlackSilence3.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public AbstractCard bond;
    public AbstractCard waltz;

    public boolean rolandAttackedThisTurn = false;

    public BlackSilence3() { this(0.0f, 0.0f); }

    public BlackSilence3(final float x, final float y) {
        super(NAME, ID, 500, 0.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BlackSilence4/Spriter/BlackSilence4.scml"));

        numAdditionalMoves = 0;
        maxAdditionalMoves = 1;
        for (int i = 0; i < maxAdditionalMoves; i++) { additionalMovesHistory.add(new ArrayList<>()); }

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
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int i) {

    }
}
