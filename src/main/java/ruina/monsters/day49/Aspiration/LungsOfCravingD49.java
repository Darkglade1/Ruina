package ruina.monsters.day49.Aspiration;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.act3.heart.LungsOfCraving;
import ruina.monsters.day49.AngelaD49;
import ruina.powers.FerventBeats;
import ruina.powers.Paralysis;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class LungsOfCravingD49 extends AbstractRuinaMonster
{
    public static final String ID = makeID(LungsOfCravingD49.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(LungsOfCraving.ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte FERVENT_BEATS = 0;
    private static final byte RETRACTING_BEATS = 1;

    private static final int FERVENTDAMAGE = 7;
    private static final int FERVENTHITS = 3;
    private static final int RETRACTINGDAMAGE = 30;
    private static final int RETRACTINGPARALYSIS = 3;

    private static final int HANKERING = 7;
    private static final int FERVENTBEATS_STRENGTH = 5;
    private static final int FERVENTBEATS_TIMER = 4;

    private AngelaD49 angela;

    public LungsOfCravingD49(AngelaD49 parent) {
        this(0.0f, 0.0f, parent);
    }
    public LungsOfCravingD49(final float x, final float y, AngelaD49 parent) {
        super(NAME, ID, 140, 0.0F, 0, 280.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Lungs/Spriter/Lungs.scml"));
        this.type = EnemyType.NORMAL;
        setHp(150);
        addMove(FERVENT_BEATS, Intent.ATTACK, 10, 3, true);
        addMove(RETRACTING_BEATS, Intent.ATTACK_DEBUFF, 30);
        angela = parent;
    }

    public void usePreBattleAction(){
        applyToTarget(this, this, new FerventBeats(this, FERVENTBEATS_TIMER));
        applyToTarget(this, this, new StrengthPower(this, FERVENTBEATS_STRENGTH));
    }
    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case FERVENT_BEATS: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(adp());
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case RETRACTING_BEATS: {
                attackAnimation(adp());
                applyToTarget(adp(), this, new Paralysis(adp(), RETRACTINGPARALYSIS));
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(FERVENT_BEATS)) {
            possibilities.add(FERVENT_BEATS);
        }
        if (!this.lastMove(RETRACTING_BEATS)) {
            possibilities.add(RETRACTING_BEATS);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "WoodStrike", enemy, this);
    }


    public void die(){
        super.die();
        angela.onMinionDeath();
    }

    public void externalDie(){
        applyToTarget(angela, this, new StrengthPower(angela, HANKERING));
        super.die();
    }
}