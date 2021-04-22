package ruina.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import ruina.RuinaMod;
import ruina.cards.Melody;
import ruina.monsters.blackSilence.blackSilence3.Angelica;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makePowerPath;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public class WhiteNoise extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(WhiteNoise.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    boolean completed = false;

    private static final int CARD_AMOUNT_NEEDED = 3;

    private Melody bond;
    private Angelica angelica;
    private static final Texture tex84 = TexLoader.getTexture(makePowerPath("WhiteEnergy84.png"));
    private static final Texture tex32 = TexLoader.getTexture(makePowerPath("WhiteEnergy32.png"));

    public WhiteNoise(Angelica owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, CARD_AMOUNT_NEEDED);
        updateDescription();
        angelica = owner;
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if (!completed && !angelica.halfDead) {
            if (card.type.equals(AbstractCard.CardType.SKILL) || card.type.equals(AbstractCard.CardType.POWER)) {
                if (amount - 1 == 0) {
                    amount = -1;
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            angelica.takeTurn();
                            isDone = true;
                        }
                    });
                    completed = true;
                } else { amount -= 1; }
            }
        }
    }

    public void atStartOfTurn() {
        amount = CARD_AMOUNT_NEEDED;

        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            flashWithoutSound();
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    if(!completed && !angelica.halfDead){ angelica.setBondIntent(); }
                    angelica.createIntent();
                    isDone = true;
                }
            });
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    amount = CARD_AMOUNT_NEEDED;
                    if(completed){
                        att(new RollMoveAction(angelica));
                        att(new AbstractGameAction() {
                            @Override
                            public void update() {
                                angelica.createIntent();
                                this.isDone = true;
                            }
                        });
                    }
                    isDone = true;
                    completed = false;
                }
            });
        }
    }

    @Override
    public void updateDescription() { description = String.format(DESCRIPTIONS[0], CARD_AMOUNT_NEEDED); }
}