package guiGame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JFrame implements ActionListener {

	public static void main(String[] args) {
		new Main();
	}

	private GamePanel gPanel;

	private JButton restart;
	private JButton reverse;
	private JButton resign;

	private Main() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		JPanel option = new JPanel();
		option.setLayout(new FlowLayout());
		restart = new JButton("最初から");
		restart.addActionListener(this);
		reverse = new JButton("待った");
		reverse.addActionListener(this);
		resign = new JButton("投了");
		resign.addActionListener(this);
		option.add(restart);
		option.add(reverse);
		option.add(resign);
		getContentPane().add(option, BorderLayout.NORTH);
		initGame();
		setSize(550, 650);
		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == restart) {
			getContentPane().remove(gPanel);
			initGame();
			setVisible(true);
		}
		else if(e.getSource() == resign) {
			gPanel.resign();
		}
		else {
			gPanel.back();
		}
	}

	private void initGame() {
		Game game = new Game();
		gPanel = new GamePanel(game);
		getContentPane().add(gPanel, BorderLayout.CENTER);
	}
}
