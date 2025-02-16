package spidersolitaire.models;


class Move {
    private Position from;
    private Position to;

    private enum Location {COLUMNS, RUNS, STOCKS};

    private static class Position {
        Location location;
        int locationIndex;
        int stackIndex;

        Position(Location location, int locationIndex, int stackIndex) {
            this.location = location;
            this.locationIndex = locationIndex;
            this.stackIndex = stackIndex;
        }

        Location getLocation() {
            return this.location;
        }

        int getLocationIndex() {
            return this.locationIndex;
        }

        int getStackIndex() {
            return this.stackIndex;
        }
    }

    Move() {
        this.from = null;
        this.to = null;
    }

    Position getFrom() {
        return this.from;
    }

    void setFrom(Location location, int locationIndex, int stackIndex) {
        this.from = new Position(location, locationIndex, stackIndex);
    }

    Position getTo() {
        return this.to;
    }

    void setTo(Location location, int locationIndex, int stackIndex) {
        this.to = new Position(location, locationIndex, stackIndex);
    }
}
