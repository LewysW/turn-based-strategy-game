public class AttackPhase {
    private AttackStage stage;
    private Territory attacking;
    private Territory defending;
    private int redDice;
    private int whiteDice;

    public AttackPhase() {
        this.stage = AttackStage.NONE_SELECTED;
        this.redDice = 1;
        this.whiteDice = 1;
    }


    public AttackStage getStage() {
        return stage;
    }

    public void setStage(AttackStage stage) {
        this.stage = stage;
    }

    public Territory getAttacking() {
        return attacking;
    }

    public void setAttacking(Territory attacking) {
        this.attacking = attacking;
    }

    public Territory getDefending() {
        return defending;
    }

    public void setDefending(Territory defending) {
        this.defending = defending;
    }

    public void incrementRed() {
        redDice++;
    }

    public void incrementWhite() {
        whiteDice++;
    }

    public void decrementRed() {
        redDice--;
    }

    public void decrementWhite() {
        whiteDice--;
    }

    public int getRedDice() {
        return redDice;
    }

    public int getWhiteDice() {
        return whiteDice;
    }

    public void setRedDice(int redDice) {
        this.redDice = redDice;
    }

    public void setWhiteDice(int whiteDice) {
        this.whiteDice = whiteDice;
    }
}
