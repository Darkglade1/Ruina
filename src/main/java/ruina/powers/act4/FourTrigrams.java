package ruina.powers.act4;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;
import ruina.RuinaMod;
import ruina.monsters.uninvitedGuests.normal.clown.Tiph;
import ruina.multiplayer.MessengerListener;
import ruina.powers.AbstractUnremovablePower;
import ruina.util.TexLoader;
import spireTogether.networkcore.P2P.P2PManager;
import spireTogether.util.SpireHelp;

import static ruina.RuinaMod.makePowerPath;
import static ruina.util.Wiz.*;

public class FourTrigrams extends AbstractUnremovablePower {

    public static final String POWER_ID = RuinaMod.makeID("FourTrigrams");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture geon84 = TexLoader.getTexture(makePowerPath("Geon84.png"));
    private static final Texture geon32 = TexLoader.getTexture(makePowerPath("Geon32.png"));

    private static final Texture gon84 = TexLoader.getTexture(makePowerPath("Gon84.png"));
    private static final Texture gon32 = TexLoader.getTexture(makePowerPath("Gon32.png"));

    private static final Texture ri84 = TexLoader.getTexture(makePowerPath("Ri84.png"));
    private static final Texture ri32 = TexLoader.getTexture(makePowerPath("Ri32.png"));

    private int trigram;

    public FourTrigrams(AbstractCreature owner, int trigram) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        this.trigram = trigram;
        this.priority = 99;

        this.region128 = new TextureAtlas.AtlasRegion(geon84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(geon32, 0, 0, 32, 32);
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL && trigram == Tiph.GEON) {
            return damage * (1 + ((float)amount / 100));
        } else {
            return damage;
        }
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL && trigram == Tiph.GON) {
            return damage * (1 - ((float)amount / 100));
        } else {
            return damage;
        }
    }

    public void changeTrigram(int trigram) {
        this.trigram = trigram;
        if (trigram == Tiph.GEON) {
            amount = Tiph.geonDamageBonus;
            this.region128 = new TextureAtlas.AtlasRegion(geon84, 0, 0, 84, 84);
            this.region48 = new TextureAtlas.AtlasRegion(geon32, 0, 0, 32, 32);
        } else if (trigram == Tiph.GON) {
            amount = Tiph.gonDamageReduction;
            this.region128 = new TextureAtlas.AtlasRegion(gon84, 0, 0, 84, 84);
            this.region48 = new TextureAtlas.AtlasRegion(gon32, 0, 0, 32, 32);
        } else if (trigram == Tiph.RI){
            amount = Tiph.riDrawBonus;
            this.region128 = new TextureAtlas.AtlasRegion(ri84, 0, 0, 84, 84);
            this.region48 = new TextureAtlas.AtlasRegion(ri32, 0, 0, 32, 32);
            applyToTarget(adp(), owner, new DrawCardNextTurnPower(adp(), amount));
        }
        updateDescription();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.onModifyPower();
                this.isDone = true;
            }
        });
    }

    @Override
    public void atEndOfRound() {
        if (this.trigram == Tiph.GEON) {
            changeTrigram(Tiph.RI);
        } else if (this.trigram == Tiph.RI) {
            changeTrigram(Tiph.GON);
        } else {
            changeTrigram(Tiph.GEON);
        }
        if (owner instanceof Tiph) {
            ((Tiph) owner).setPhase(trigram);
        }
        this.flash();
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (RuinaMod.isMultiplayerConnected() && info.owner == adp()) {
            P2PManager.SendData(MessengerListener.request_playerDamageTiph, owner.currentHealth - damageAmount, owner.currentBlock, SpireHelp.Gameplay.CreatureToUID(owner), SpireHelp.Gameplay.GetMapLocation());
        }
        return damageAmount;
    }

    @Override
    public void stackPower(int stackAmount) {
        // doesn't stack
    }

    @Override
    public void updateDescription() {
        if (trigram == Tiph.GEON) {
            name = DESCRIPTIONS[0];
            description = DESCRIPTIONS[1] + amount + DESCRIPTIONS[2];
        } else if (trigram == Tiph.GON) {
            name = DESCRIPTIONS[3];
            description = DESCRIPTIONS[4] + amount + DESCRIPTIONS[5];
        } else {
            name = DESCRIPTIONS[6];
            description = DESCRIPTIONS[7] + amount + DESCRIPTIONS[8];
        }
        description += DESCRIPTIONS[9];
    }
}
