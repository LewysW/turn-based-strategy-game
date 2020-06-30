public class TacticalMovePhase {
    private MoveStage stage;
    private Territory source;
    private Territory destination;

    public TacticalMovePhase() {
        this.stage = MoveStage.NONE_SELECTED;
    }

    public MoveStage getStage() {
        return stage;
    }

    public Territory getSource() {
        return source;
    }

    public Territory getDestination() {
        return destination;
    }

    public void setStage(MoveStage stage) {
        this.stage = stage;
    }

    public void setSource(Territory source) {
        this.source = source;
    }

    public void setDestination(Territory destination) {
        this.destination = destination;
    }
}
