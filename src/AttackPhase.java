public class AttackPhase {
    private AttackStage stage;
    private Territory attacking;
    private Territory defending;

    public AttackPhase() {
        this.stage = AttackStage.NONE_SELECTED;
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
}
