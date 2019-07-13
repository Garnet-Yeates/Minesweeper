package wit.edu.garnetyeates.minesweeper;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;

public class GameGUI extends JFrame
{
	private static final long serialVersionUID = 6380643699455578877L;

	public static void main(String[] args)
	{
		EventQueue.invokeLater(() ->
		{
			GamePanel gamePanel = new GamePanel();
			new GameGUI(gamePanel);
		});
	}

	public GameGUI(GamePanel panel)
	{	
		this.addKeyListener(panel);
		Dimension prefSize = new Dimension(GamePanel.PREF_WIDTH + 16 - 10, GamePanel.PREF_HEIGHT + 39 - 10);
		panel.setPreferredSize(prefSize);
		panel.setSize(prefSize);
		panel.addMouseListener(panel);
		this.setPreferredSize(prefSize);
		this.setSize(prefSize);
		this.getContentPane().add(panel);
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(panel);		
		System.out.println(GamePanel.PREF_WIDTH - panel.getWidth());
		System.out.println(GamePanel.PREF_HEIGHT - panel.getHeight());

	}
}
