package ruina.monsters.uninvitedGuests.normal.argalia.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Argalia;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_ResonantScythe extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_ResonantScythe.class.getSimpleName());

    private Argalia parent;

    public CHRBOSS_ResonantScythe(Argalia parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.scytheDamage;
        magicNumber = baseMagicNumber = parent.scytheDamageMultiplier;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        if (parent.additionalMoves.size() > 0) {
            if (parent.additionalMoves.get(0).nextMove == Argalia.SCYTHE) {
                this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
            }
        }
    }

    @Override
    public void upp() { }
}