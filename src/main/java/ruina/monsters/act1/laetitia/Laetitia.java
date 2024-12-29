package ruina.monsters.act1.laetitia;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.BetterSpriterAnimation;
import ruina.cards.Gift;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act1.LonelyIsSad;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Laetitia extends AbstractRuinaMonster {
    public static final String ID = makeID(Laetitia.class.getSimpleName());

    private static final byte GIFT = 0;
    private static final byte FUN = 1;

    private final int DAMAGE_INCREASE;
    private final int STATUS = 1;

    private final AbstractCard gift = new Gift();

    public Laetitia() {
        this(140.0f, 0.0f);
    }

    public Laetitia(final float x, final float y) {
        super(ID, ID, 100, 0.0F, 0, 200.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Laetitia/Spriter/Laetitia.scml"));
        setHp(calcAscensionTankiness(70));
        addMove(GIFT, Intent.ATTACK_DEBUFF, calcAscensionDamage(6));
        addMove(FUN, Intent.ATTACK, calcAscensionDamage(8));

        if (AbstractDungeon.ascensionLevel >= 18) {
            gift.upgrade();
            DAMAGE_INCREASE = 33;
        } else {
            DAMAGE_INCREASE = 50;
        }
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning1");
        atb(new ApplyPowerAction(this, this, new LonelyIsSad(this, DAMAGE_INCREASE)));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case GIFT: {
                attackAnimation(adp());
                dmg(adp(), info);
                intoDiscardMo(gift.makeStatEquivalentCopy(), STATUS, this);
                resetIdle();
                break;
            }
            case FUN: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastTwoMoves(GIFT)) {
            setMoveShortcut(FUN);
        } else {
            setMoveShortcut(GIFT);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        String textureString = makeUIPath("detailedIntents/Gift.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case GIFT: {
                DetailedIntent detail = new DetailedIntent(this, STATUS, texture, DetailedIntent.TargetType.DISCARD_PILE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof GiftFriend || mo instanceof WitchFriend) {
                if (mo instanceof GiftFriend) {
                    ((GiftFriend) mo).spawnSpider = false;
                }
                atb(new SuicideAction(mo));
            }
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "LaetitiaAtk", enemy, this);
    }

}