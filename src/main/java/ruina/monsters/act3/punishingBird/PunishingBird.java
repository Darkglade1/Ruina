package ruina.monsters.act3.punishingBird;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.PunishingBirdPunishmentPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class PunishingBird extends AbstractRuinaMonster {
    public static final String ID = makeID(PunishingBird.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte PECK = 0;
    private static final byte PUNISHMENT = 1;
    private int chains = 3;
    public AbstractMonster[] minions = new AbstractMonster[3];

    public PunishingBird() {
        this(150.0f, 0.0f);
    }

    public PunishingBird(final float x, final float y) {
        super(NAME, ID, 500, -5.0F, 0, 160.0f, 185.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ScytheApostle/Spriter/ScytheApostle.scml"));
        this.type = EnemyType.ELITE;
        setHp(maxHealth);
        addMove(PECK, Intent.ATTACK, calcAscensionDamage(2), 3, true);
        addMove(PUNISHMENT, Intent.ATTACK, calcAscensionDamage(50));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;
        if (info.base > -1) { info.applyPowers(this, adp()); }
        switch (nextMove) {
            case PECK:
                for (int i = 0; i < multiplier; i++) { dmg(adp(), info); }
                break;
            case PUNISHMENT:
                dmg(adp(), info);
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        PunishingBirdPunishmentPower punishment = (PunishingBirdPunishmentPower) this.getPower(PunishingBirdPunishmentPower.POWER_ID);
        if(punishment != null){
            if(punishment.getPunishment()){ setMoveShortcut(PUNISHMENT, MOVES[1]); }
            else { setMoveShortcut(PECK, MOVES[0]); }
        }
        else { setMoveShortcut(PECK, MOVES[0]); }
    }

    @Override
    public void usePreBattleAction() {
        atb(new ApplyPowerAction(this, this, new PunishingBirdPunishmentPower(this)));
        Summon();
    }

    public void Summon() {
        float xPos_Farthest_L = -450F;
        float xPos_Middle_L = -300F;
        float xPos_Short_L = -150F;
        for (int i = 0; i < minions.length; i++) {
            if (minions[i] == null) {
                AbstractMonster keeper;
                if (i == 0) { keeper = new Keeper(xPos_Farthest_L, 0.0f, this);
                } else if (i == 1) { keeper = new Keeper(xPos_Middle_L, 0.0f, this);
                } else { keeper = new Keeper(xPos_Short_L, 0.0f, this); }
                atb(new SpawnMonsterAction(keeper, true));
                atb(new UsePreBattleActionAction(keeper));
                if (!firstMove) {
                    keeper.rollMove();
                    keeper.createIntent();
                }
                minions[i] = keeper;
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof Keeper) { atb(new SuicideAction(mo)); }
        }
        // funky win anim goes here
    }

    public void decreaseChains(){
        chains -= 1;
        if(chains == 0){ super.die(); }
        // animation stuff
    }

}