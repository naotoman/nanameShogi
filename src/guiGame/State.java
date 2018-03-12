package guiGame;

enum State {
	SENTE, GOTE, EMPTY, WALL;

	State toggle() {
		assert this!= EMPTY && this != WALL;
		return (this == SENTE) ? GOTE : SENTE;
	}

}
