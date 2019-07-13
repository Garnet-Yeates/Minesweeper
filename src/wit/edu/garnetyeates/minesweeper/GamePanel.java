package wit.edu.garnetyeates.minesweeper;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements MouseListener, KeyListener
{
	private static final long serialVersionUID = 1552746400473185110L;
	
	/** How many pixels wide and tall are each tile? */
	public static final int TILE_SIZE = 45;
	
	/** What is the thickness of the grid lines between each tile? */
	public static final int SPACE_SIZE = 3;
	
	/** How many tiles are there going horizontally? */
	public static final int H_TILES = 32;
	
	/** How many tiles are there going vertically? */
	public static final int V_TILES = 16;
	
	/** How many grid lines are there, going left to right? */
	public static final int H_SPACES = H_TILES + 1;
	
	/** How many grid lines are there, going up to down? */
	public static final int V_SPACES = V_TILES + 1;
	
	/** Based on all of the above fields, how many pixels wide should this JPanel be? */
	public static final int PREF_WIDTH = TILE_SIZE*H_TILES + SPACE_SIZE*H_SPACES;
	
	/** Based on all of the above fields, how many pixels tall should this JPanel be? */
	public static final int PREF_HEIGHT = TILE_SIZE*V_TILES + SPACE_SIZE*V_SPACES;
	
	/** An array representation of the tiles in the game */
	public final Tile[][] mineField = new Tile[H_TILES][V_TILES];
			
	/** For every 1 tile how many mines should there be? */
	public static final double MINE_RARITY_PERCENT = 0.12;
	// 0.12 , 0.16, 0.22
	
	/** Universal seeded (or unseeded) random used by all classes in this program */
	public static final Random R = new Random(3);

	/**
	 * Once this JPanel is constructed the game is pre initialized. During pre initialization,
	 * images are loaded, and static references are set. Then {@link #initGame()} is called to
	 * fully initialize the game
	 */
	public GamePanel()
	{
		firstClick = true;
		initNumberIcons();
		Tile.setGamePanel(this);
		initGame();
	}
	
	/**
	 * Fully initializes the game. If this is called again during a game it will effectively restart the game,
	 * with {@link #firstClick} being the only impactful field left unchanged. This method initializes the tiles
	 * and randomly places mines on each tile, assigns the numbers tha appear on each tiles, and counts the number
	 * of tiles/bombs in the game
	 */
	public void initGame()
	{
		frozen = false;
		initTiles();
		Tile.assignTileNumbers();
		calculateNumTilesAndMines();
		System.out.println("You have " + howManyMinesUnflagged() + " mines left to flag");
	}
	
	/**
	 * Initializes the tiles of the game. This method loops through the {@link #mineField} array and fills each
	 * space with a new Tile instance. Upon a tile's creation, there is a chance that there will be a mine placed
	 * on it, based off of {@link #MINE_RARITY_PERCENT}
	 */
	private void initTiles()
	{
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		int xPos = SPACE_SIZE + 0;
		for (int x = 0; x < mineField.length; x++)
		{
			int yPos = SPACE_SIZE + 0;
			for (int y = 0; y < mineField[x].length; y++)
			{
				Tile t = new Tile(x, y, xPos, yPos);
				mineField[x][y] = t;
				tiles.add(t);
				yPos += SPACE_SIZE + TILE_SIZE;
			}
			xPos += SPACE_SIZE + TILE_SIZE;
		}
		Tile.setTileList(tiles);
	}
	
	public final ImageIcon flagIcon = loadImage("flag.png");
	public final ImageIcon diffusedIcon = loadImage("redflag.png");
	
	public final ImageIcon bombIcon = loadImage("bomb.png");
	public final ImageIcon redBombIcon = loadImage("redbomb.png");
	
	public final HashMap<Integer, ImageIcon> numberIcons = new HashMap<>();
	
	/**
	 * Initializes the {@link #numberIcons} HashMap. This makes it easy to obtain
	 * the image for each respective tile number
	 */
	private void initNumberIcons()
	{
		numberIcons.put(1, loadImage("1.png"));
		numberIcons.put(2, loadImage("2.png"));
		numberIcons.put(3, loadImage("3.png"));
		numberIcons.put(4, loadImage("4.png"));
		numberIcons.put(5, loadImage("5.png"));
		numberIcons.put(6, loadImage("6.png"));		
	}
	
	public static final Color COL_GRID = new Color(50, 50, 50);
	public static final Color COL_HIDDEN_TILE = new Color(100, 100, 100);
	public static final Color COL_BLANK_TILE = new Color(255, 246, 224);
	public static final Color COL_BOMB = new Color(0, 0, 0);

	public static final Color RED = new Color(255, 94, 94);
	public static final Color ORANGE = new Color(255, 177, 94);
	public static final Color YELLOW = new Color(255, 244, 94);
	public static final Color GREEN = new Color(129, 255, 94);
	public static final Color BLUE = new Color(48, 164, 252);
	public static final Color PURP = new Color(255, 61, 219);
	
	@Override 
	public void paint(Graphics g)
	{
		// PAINT GRIDLINES
		g.setColor(COL_GRID);
		g.fillRect(0, 0, PREF_WIDTH, PREF_HEIGHT);
		
		for (Tile t : Tile.tiles)
		{
			if (t.isFlagged())
			{
				g.setColor(COL_BLANK_TILE);
				g.fillRect(t.getPixelX(), t.getPixelY(), TILE_SIZE, TILE_SIZE);
				(!t.isDefused() ? flagIcon : diffusedIcon).paintIcon(this, g.create(), t.getPixelX(), t.getPixelY());
			}
			else
			{
				if (!t.isMine())
				{
					Color col = determineTileColor(t);
				//	col = COL_BLANK;
					g.setColor(t.isRevealed() ? col : COL_HIDDEN_TILE);
					g.fillRect(t.getPixelX(), t.getPixelY(), TILE_SIZE, TILE_SIZE);
					
					if (numberIcons.containsKey(t.getNumber()) && t.isRevealed())
					{
						numberIcons.get(t.getNumber()).paintIcon(this, g.create(), t.getPixelX(), t.getPixelY());
					}	
				}
				else
				{
					if (t.isRevealed())
					{
						g.setColor(COL_BLANK_TILE);
						g.fillRect(t.getPixelX(), t.getPixelY(), TILE_SIZE, TILE_SIZE);
						(t.isRedMine() ? redBombIcon : bombIcon).paintIcon(this, g.create(), t.getPixelX(), t.getPixelY());

					}
					else
					{
						g.setColor(COL_HIDDEN_TILE);
						g.fillRect(t.getPixelX(), t.getPixelY(), TILE_SIZE, TILE_SIZE);
					}

				}
			}
		}
		// TODO Auto-generated method stub
	}

	public static Color determineTileColor(Tile t)
	{
		int numBombsNear = t.getNumber();
		Color col = null;
		switch (numBombsNear)
		{
		case 0:
			col = COL_BLANK_TILE;
			break;
		case 1:
			col = GREEN;
			break;
		case 2:
			col = YELLOW;
			break;
		case 3:
			col = ORANGE;
			break;
		case 4:
			col = RED;
			break;
		case 5:
			col = BLUE;
			break;
		case 6:
			col = PURP;
			break;
		default:
			col = Color.WHITE;
			break;
		}		
		return col;
	}
	
	public Tile tileAt(int x, int y)
	{
		try
		{
			return mineField[x][y];
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/** If this is true, no keyboard/mouse inputs will be registered by the program */
	public boolean frozen = false;
	
	public void freezeInputs()
	{
		frozen = true;
	}

	/** The program keeps track of whether or not the user has clicked yet, because if a player
	 *  loses the game on their first click then it will remake the game and make sure the tile
	 *  that they clicked doesn't have a bomb on it
	 */
	private boolean firstClick = true;	
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (!frozen)
		{
			int x = e.getX();
			int y = e.getY();
			Tile clicked = null;
			for (Tile t : Tile.tiles)
			{
				if (t.getPixelX() < x && x < t.getPixelX() + TILE_SIZE && t.getPixelY() < y && y < t.getPixelY() + TILE_SIZE)
				{
					clicked = t;
				}
			}
			if (clicked != null)
			{
				clicked.onClick(holdingControl || e.getButton() == 3);
			}
			repaint();
		}
	}	
	private boolean holdingControl = false;
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			holdingControl = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			holdingControl = false;
		}		
	}

	public void onTileReveal()
	{
		tilesToReveal--;
		if (tilesToReveal == 0)
		{
			onGameWin();
		}
		
	}
	
	private int tilesToReveal = 0;
	private int playerMineTracker = 0;
	
	public void calculateNumTilesAndMines()
	{
		tilesToReveal = 0;
		playerMineTracker = 0;
		for (Tile t : Tile.tiles)
		{
			if (!t.isMine() && !t.isRevealed())
			{
				tilesToReveal++;
			}
			else if (!t.isRevealed())
			{
				playerMineTracker++;
			}
		}
	}
	
	public int howManyMinesUnflagged()
	{
		return playerMineTracker;
	}

	public void onFlagTile(boolean flagged)
	{
		playerMineTracker += flagged == true ? -1 : 1;
		System.out.println("You have " + howManyMinesUnflagged() + " mines left to flag");
	}

	public void onPlayerFirstClick()
	{
		firstClick = false;
	}
	
	public boolean hasPlayerClickedYet()
	{
		return firstClick;
	}
	
	private Location safeSpot = null;
	
	public Location getSafeSpot()
	{
		return safeSpot;
	}
	
	public boolean hasSafeSpot()
	{
		return safeSpot != null;
	}
	
	public void onFirstClickLoss(Tile tileThatWasClicked)
	{
		System.out.println("You would have normally lost on that first click but im a good coder so fuck you");
		int x = tileThatWasClicked.getArrayX();
		int y = tileThatWasClicked.getArrayY();
		safeSpot = new Location(x, y);
		initGame();
		tileAt(x, y).onClick(false);
		repaint();
	}
	
	public void onGameLoss()
	{
		Tile.revealAllTiles();
		Tile.checkFlagAccuracy();
		repaint();
		freezeInputs();
		System.out.println("ITS F OVER, AND STELLA THINKS U R A LOSER");
	}
	
	public void onGameWin()
	{
		System.out.println("YOU F WIN");
	}
	
	public ImageIcon loadImage(String fileName)
	{
	  BufferedImage buff = null;
	  try
	  {
		  buff = ImageIO.read(getClass().getResourceAsStream(fileName));
	  }
	  catch (IOException e)
	  {
	     e.printStackTrace();
	  }
	  return new ImageIcon(buff);
	}

	// UNUSED METHODS
	
	@Override public void mouseEntered(MouseEvent arg0) { } @Override public void mouseExited(MouseEvent arg0) { } @Override public void mousePressed(MouseEvent arg0) { } @Override public void mouseReleased(MouseEvent arg0) { } 	@Override public void keyTyped(KeyEvent arg0) { }
}