package guiGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.stream.IntStream;

import javax.swing.JPanel;

class GamePanel extends JPanel implements MouseListener {

	private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
	private static final Color BOARD_COLOR = new Color(248, 220, 133);
	private static final Color PIECE_COLOR = new Color(209, 145, 71);
	private static final Color EDGE_COLOR = new Color(107, 64, 45);
	private static final Color CHOSEN_COLOR = new Color(229, 69, 0);

	private static final Font TEXT_FONT = new Font(null, 0, 25);

	private static final int SQUARE_H = 50;
	private static final int SQUARE_W = 48;
	private static final int X0 = 50;
	private static final int Y0 = 50;

	private Graphics g;
	private Game game;

	private int selected = -1;


	GamePanel(Game game) {
		this.game = game;
		addMouseListener(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		this.g = g;
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(BOARD_COLOR);
		g.fillRect(X0, Y0, 9*SQUARE_W, 9*SQUARE_H);

		g.setColor(Color.BLACK);
		for(int i=0; i<10; i++) {
			g.drawLine(X0, Y0 + i*SQUARE_H, X0 + 9*SQUARE_W, Y0 + i*SQUARE_H);
			g.drawLine(X0 + i*SQUARE_W, Y0, X0 + i*SQUARE_W, Y0 + 9*SQUARE_H);
		}

		//駒を初期配置
		IntStream.iterate(1, i->i+2).limit(5).forEach(i -> drawPiece(1, i, State.GOTE));
		IntStream.iterate(2, i->i+2).limit(4).forEach(i -> drawPiece(2, i, State.GOTE));
		IntStream.iterate(1, i->i+2).limit(5).forEach(i -> drawPiece(9, i, State.SENTE));
		IntStream.iterate(2, i->i+2).limit(4).forEach(i -> drawPiece(8, i, State.SENTE));


		writeTurn(State.SENTE);
		writeMoves(0);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		g = getGraphics();
		int x = e.getX();
		int y = e.getY();
		int rowM = (y - Y0) / SQUARE_H + 1;
		int colM = (x - X0) / SQUARE_W + 1;
		int[] rcS = Game.decode(selected);
		
		//click outside of the board
		if(isOut(x, y)) {
			if(selected < 0) {return;}
			fillSquare(rcS[0], rcS[1], BOARD_COLOR);
			drawPiece(rcS[0], rcS[1], game.getPiece(rcS[0], rcS[1]));
			selected = -1;
			return;
		}
		
		State stM = game.getPiece(rowM, colM);
		State turn = game.getTurn();
		//click own piece on the board
		if(stM == turn) {
			fillSquare(rowM, colM, CHOSEN_COLOR);
			drawPiece(rowM, colM, stM);
			if(selected < 0) {
				selected = Game.encode(rowM, colM);
				return;
			}
			fillSquare(rcS[0], rcS[1], BOARD_COLOR);
			drawPiece(rcS[0], rcS[1], game.getPiece(rcS[0], rcS[1]));
			if(selected == Game.encode(rowM, colM)) {
				selected = -1;
			}
			else {
				selected = Game.encode(rowM, colM);
			}
			return;
		}
		//click others
		if(selected < 0) {
			return;
		}
		if(game.canMove(rcS[0], rcS[1], rowM, colM)) {
			game.move(rcS[0], rcS[1], rowM, colM);
			fillSquare(rcS[0], rcS[1], BOARD_COLOR);
			drawPiece(rowM, colM, turn);
			selected = -1;
			State log = game.judge();
			fillBelow();
			if(log == State.WALL) {
				writeTurn(game.getTurn());
				writeMoves(game.getMoves());
				return;
			}
			removeMouseListener(this);
			g.setColor(Color.BLACK);
			g.setFont(TEXT_FONT);
			g.drawString(game.getMoves() + "手まで", 400, 570);
			g.setFont(new Font(null, 0, 40));
			switch(log) {
			case SENTE :
				g.setColor(Color.RED);
				g.drawString("先手の勝ち！", 10, 550);
				break;
			case GOTE :
				g.setColor(Color.RED);
				g.drawString("後手の勝ち！", 10, 550);
				break;
			case EMPTY :
				g.setColor(Color.BLUE);
				g.drawString("引き分け", 10, 550);
			default :
			}
		}
		else {
			fillSquare(rcS[0], rcS[1], BOARD_COLOR);
			drawPiece(rcS[0], rcS[1], turn);
			selected = -1;
		}
	}

	void resign() {
		g = getGraphics();
		fillBelow();
		removeMouseListener(this);
		g.setColor(Color.BLACK);
		g.setFont(TEXT_FONT);
		g.drawString(game.getMoves() + "手まで", 400, 570);
		g.setFont(new Font(null, 0, 40));
		State turn = game.getTurn();
		switch(turn) {
		case GOTE :
			g.setColor(Color.RED);
			g.drawString("先手の勝ち！", 10, 550);
			break;
		case SENTE :
			g.setColor(Color.RED);
			g.drawString("後手の勝ち！", 10, 550);
			break;
		default :
		}
	}

	void back() {
		if((game.canBack())) {
			if(selected >= 0) {
				int[] rcS = Game.decode(selected);
				fillSquare(rcS[0], rcS[1], BOARD_COLOR);
				drawPiece(rcS[0], rcS[1], game.getTurn());
				selected = -1;
			}
			int[] lastMove = game.getLastMove();
			game.back();
			int[] to = Game.decode(lastMove[1]);
			int[] from = Game.decode(lastMove[0]);
			fillSquare(to[0], to[1], BOARD_COLOR);
			drawPiece(from[0], from[1], game.getTurn());
			fillBelow();
			writeTurn(game.getTurn());
			writeMoves(game.getMoves());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}


	private void drawPiece(int row, int col, State st) {
		assert st != State.EMPTY && st != State.WALL;
		g.setColor(PIECE_COLOR);
		int xc = X0 + (col-1)*SQUARE_W + SQUARE_W/2;
		int yc = Y0 + (row-1)*SQUARE_H + SQUARE_H/2;
		int sgn = (st == State.GOTE) ? 1 : -1;
		int[] x5 = new int[5];
		int[] y5 = new int[5];
		x5[0] = xc - sgn*SQUARE_W/4;
		x5[1] = xc - sgn*SQUARE_W/6;
		x5[2] = xc;
		x5[3] = xc + sgn*SQUARE_W/6;
		x5[4] = xc + sgn*SQUARE_W/4;
		y5[0] = yc - sgn*SQUARE_H/3;
		y5[1] = yc + sgn*SQUARE_H/6;
		y5[2] = yc + sgn*SQUARE_H/4;
		y5[3] = yc + sgn*SQUARE_H/6;
		y5[4] = yc - sgn*SQUARE_H/3;
		g.fillPolygon(x5, y5, 5);
		g.setColor(EDGE_COLOR);
		g.drawPolygon(x5, y5, 5);
		int left = (sgn>0) ? 0 : 4;
		g.setColor(Color.BLACK);
		g.fillRect(x5[left], y5[left], Math.abs(x5[0]-x5[4]), 2);
	}

	private void fillBelow() {
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(200, 550, 400, 200);
	}

	private void writeTurn(State st) {
		String str = (st == State.SENTE) ? "先手番" : "後手番";
		g.setColor(Color.BLACK);
		g.setFont(TEXT_FONT);
		g.drawString(str, 260, 570);
	}

	private void writeMoves(int moves) {
		g.setColor(Color.BLACK);
		g.setFont(TEXT_FONT);
		g.drawString(moves+1 + "手目", 440, 570);
	}

	private void fillSquare(int row, int col, Color back) {
		int x = X0 + (col-1)*SQUARE_W;
		int y = Y0 + (row-1)*SQUARE_H;
		g.setColor(back);
		g.fillRect(x, y, SQUARE_W, SQUARE_H);
		g.setColor(Color.BLACK);
		g.drawLine(X0, y, X0+SQUARE_W*9, y);
		g.drawLine(X0, y+SQUARE_H, X0+SQUARE_W*9, y+SQUARE_H);
		g.drawLine(x, Y0, x, Y0+9*SQUARE_H);
		g.drawLine(x+SQUARE_W, Y0, x+SQUARE_W, Y0+9*SQUARE_H);
	}

	private boolean isOut(int x, int y) {
		return x<=X0 || x>=X0+9*SQUARE_W || y<=Y0 || y>= Y0+9*SQUARE_H;
	}
	
	
}
