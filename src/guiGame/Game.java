package guiGame;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.IntStream;

class Game {

	static int encode(int row, int col) {
		return (row-1)*9 + col-1;
	}

	static int[] decode(int code) {
		int[] rc = new int[2];
		rc[0] = code / 9 + 1;
		rc[1] = code % 9 + 1;
		return rc;
	}

	private State[][] board = new State[11][11];
	private State turn;
	private int moves;

	private Deque<Integer> recordFrom = new ArrayDeque<>();
	private Deque<Integer> recordTo = new ArrayDeque<>();

	Game() {
		initBoard();
		turn = State.SENTE;
		moves = 0;
	}

	State getPiece(int row, int col) {
		assert row>=1 && row<=9 && col>=1 && col<=9;
		return board[row][col];
	}

	State getTurn() {
		return turn;
	}

	int getMoves() {
		return moves;
	}

	boolean canBack() {
		return !recordFrom.isEmpty();
	}

	boolean canMove(int fromR, int fromC, int toR, int toC) {
		assert fromR>=1 && fromR<=9 && fromC>=1 && fromC<=9;
		assert toR>=1 && toR<=9 && toC>=1 && toC<=9;
		assert !(fromR==toR && fromC==toC);
		State from = getPiece(fromR, fromC);
		assert from == State.SENTE || from == State.GOTE;
		State to = getPiece(toR, toC);
		if(to != State.EMPTY) {
			return false;
		}
		int dx = toR - fromR;
		int dy = toC - fromC;
		if(dx*dx != dy*dy || dx*dx == 1) {
			return false;
		}
		int sigX = (dx>0) ? 1 : -1;
		int sigY = (dy>0) ? 1 : -1;
		for(int i=1; i<sigX*dx; i++) {
			if(board[fromR+sigX*i][fromC+sigY*i] == State.EMPTY) {
				return false;
			}
		}
		return true;
	}

	int[] getLastMove() {
		int[] last = new int[2];
		last[0] = recordFrom.peekFirst();
		last[1] = recordTo.peekFirst();
		return last;
	}

	void back() {
		turn = turn.toggle();
		moves--;
		int[] to = decode(recordTo.poll());
		int[] from = decode(recordFrom.poll());
		board[to[0]][to[1]] = State.EMPTY;
		board[from[0]][from[1]] = turn;
	}

	void move(int fromR, int fromC, int toR, int toC) {
		board[fromR][fromC] = State.EMPTY;
		board[toR][toC] = turn;
		turn = turn.toggle();
		moves++;
		recordFrom.push(encode(fromR, fromC));
		recordTo.push(encode(toR, toC));
	}

	State judge() {
		if(turn == State.GOTE) {
			return State.WALL;
		}
		int senteGoal = 0;
		int goteGoal = 0;
		for(int i=1; i<=9; i++) {
			if(board[1][i] == State.SENTE) senteGoal++;
			if(board[2][i] == State.SENTE) senteGoal++;
			if(board[8][i] == State.GOTE) goteGoal++;
			if(board[9][i] == State.GOTE) goteGoal++;
		}
		if(senteGoal == 9 && goteGoal < 9) {
			return State.SENTE;
		}
		if(senteGoal == 9) {
			return State.EMPTY;
		}
		if(goteGoal == 9) {
			return State.GOTE;
		}
		return State.WALL;
	}

	private void initBoard() {
		for(int i=0; i<11; i++) {
			for(int j=0; j<11; j++) {
				board[i][j] = State.WALL;
				if(i>0 && i<10 && j>0 && j<10) {
					board[i][j] = State.EMPTY;
				}
			}
		}
		IntStream.iterate(1, i->i+2).limit(5).forEach(i -> board[1][i] = State.GOTE);
		IntStream.iterate(2, i->i+2).limit(4).forEach(i -> board[2][i] = State.GOTE);
		IntStream.iterate(1, i->i+2).limit(5).forEach(i -> board[9][i] = State.SENTE);
		IntStream.iterate(2, i->i+2).limit(4).forEach(i -> board[8][i] = State.SENTE);
	}

}
