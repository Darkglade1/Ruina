package ruina.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.TexLoader;
import ruina.vfx.FlexibleFlashAtkImgEffect;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeVfxPath;
import static ruina.util.Wiz.*;

public class EternalRest extends AbstractRuinaCard {
    public static final String ID = makeID(EternalRest.class.getSimpleName());
    private static final int DAMAGE = 4;
    private static final int UP_DAMAGE = 1;
    private static final int HITS = 5;

    public EternalRest() {
        super(ID, 0, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ENEMY);
        baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = HITS;
        selfRetain = true;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < magicNumber; i++) {
            if (i % 2 == 0) {
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractRuinaMonster.playSound("FuneralAtkWhite");
                        AbstractDungeon.effectList.add(new FlexibleFlashAtkImgEffect(m.hb.cX, m.hb.cY, TexLoader.getTexture(makeVfxPath("butterfly_white.png"))));
                        this.isDone = true;
                    }
                });
            } else {
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractRuinaMonster.playSound("FuneralAtkBlack");
                        AbstractDungeon.effectList.add(new FlexibleFlashAtkImgEffect(m.hb.cX, m.hb.cY, TexLoader.getTexture(makeVfxPath("butterfly_black.png"))));
                        this.isDone = true;
                    }
                });
            }
            dmg(m, AbstractGameAction.AttackEffect.NONE);
            waitAnimation(0.25f, m);
        }
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
    }

    private void waitAnimation(float time, AbstractCreature enemy) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (enemy == null) {
                    att(new VFXActionButItCanFizzle(adp(), new WaitEffect(), time));
                } else if (!enemy.isDeadOrEscaped()) {
                    att(new VFXActionButItCanFizzle(adp(), new WaitEffect(), time));
                }
                this.isDone = true;
            }
        });
    }

}