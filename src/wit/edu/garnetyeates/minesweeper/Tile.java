package wit.edu.garnetyeates.minesweeper;
import java.util.ArrayList;
import java.util.List;

public class Tile
{
	public static ArrayList<Tile> tiles;
	
	private static GamePanel panel = null;
	
	private boolean flagged;
	private boolean revealed;
	private boolean defused;
	private boolean redMine;
	private boolean mine;
	private int number = 0;
	
	private int arrX;
	private int arrY;
	
	private int pixelX;
	private int pixelY;
	
	public Tile(int x, int y, int pixelX, int pixelY)
	{
		double randMax = (1.00 / GamePanel.MINE_RARITY_PERCENT); 
		mine = GamePanel.R.nextInt((int) (randMax*10)) < 10 ? true : false;
		this.arrX = x;
		this.arrY = y;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		revealed = false;
		flagged = false;
		if (panel.hasSafeSpot() && panel.getSafeSpot().x == x && panel.getSafeSpot().y == y)
		{
			mine = false;
		}
	}
	
	public static void setGamePanel(GamePanel panel)
	{
		Tile.panel = panel;
	}
	
	public static void assignTileNumbers()
	{
		for (Tile t : tiles)
		{
			if (!t.mine)
			{
				int nearbyMines = 0;
				for (Tile adjTile : t.getAdjacentTiles())
				{
					nearbyMines += adjTile.mine ? 1 : 0;
				}
				t.number = nearbyMines;
			}
		}
	}
	
	public List<Tile> getAdjacentTiles()
	{
		ArrayList<Tile> rawList = new ArrayList<Tile>();
		rawList.add(tileAt(arrX - 1, arrY - 1));
		rawList.add(tileAt(arrX - 1, arrY));
		rawList.add(tileAt(arrX - 1, arrY + 1));
		rawList.add(tileAt(arrX, arrY + 1));
		rawList.add(tileAt(arrX, arrY - 1));
		rawList.add(tileAt(arrX + 1, arrY - 1));
		rawList.add(tileAt(arrX + 1, arrY));
		rawList.add(tileAt(arrX + 1, arrY + 1));
		ArrayList<Tile> list = new ArrayList<Tile>();
		for (Tile t : rawList)
		{
			if (t != null)
			{
				list.add(t);
			}
		}
		return list;
	}
	
	public boolean isMine()
	{
		return mine;
	}
	
	public static Tile tileAt(int x, int y)
	{
		return panel.tileAt(x, y);
	}

	public static void setTileList(ArrayList<Tile> tiles)
	{
		Tile.tiles = tiles;
	}
	
	public int getNumber()
	{
		return number;
	}
	
	public int getPixelX()
	{
		return pixelX;
	}
	
	public int getPixelY()
	{
		return pixelY;
	}
	
	public int getArrayX()
	{
		return arrX;
	}
	
	public int getArrayY()
	{
		return arrY;
	}

	public void onClick(boolean flag)
	{
		if (flag && !revealed)
		{
			flagged = flagged == true ? false : true;
			panel.onFlagTile(flagged);
		}
		else if (!flagged)
		{
			if (mine)
			{
				if (panel.hasPlayerClickedYet())
				{
					panel.onFirstClickLoss(this);
				}
				else
				{
					revealed = true;
					redMine = true;
					panel.onGameLoss();
				}
			}
			else
			{
				panel.onPlayerFirstClick();
				if (!revealed && number == 0)
				{
					recursiveReveal(this);
				}
				else if (!revealed)
				{
					revealTile();
				}	
			}
	
		}
	}
	
	public void recursiveReveal(Tile orig)
	{
		recursiveReveal(null, orig);
	}
	
	public void recursiveReveal(ArrayList<Tile> alreadyRevealedTiles, Tile orig)
	{
		orig.revealTile();
		alreadyRevealedTiles = alreadyRevealedTiles == null ? new ArrayList<Tile>() : alreadyRevealedTiles;		
		alreadyRevealedTiles.add(orig);
		ArrayList<Tile> adjacentUnrevealedTiles = new ArrayList<Tile>();
		for (Tile t : orig.getAdjacentTiles())
		{
			if (!alreadyRevealedTiles.contains(t))
			{
				alreadyRevealedTiles.add(t);
				adjacentUnrevealedTiles.add(t);
			}
		}
	
		for (Tile t : adjacentUnrevealedTiles)
		{
			if (!t.flagged)
			{
				if (t.number == 0)
				{
					recursiveReveal(alreadyRevealedTiles, t);
				}
				else
				{
					t.revealTile();
				}
			}
		}
	}
	
	private void revealTile()
	{
		panel.onTileReveal();
		revealed = true;
	}
	
	public static void revealAllTiles()
	{
		for (Tile t : tiles)
		{
			t.revealed = true;
		}
	}
	
	public static void checkFlagAccuracy()
	{
		for (Tile t : tiles)
		{
			t.defused = t.flagged && t.mine;
		}
	}

	public boolean isRevealed()
	{
		return revealed;
	}

	public boolean isFlagged()
	{
		return flagged;
	}
	
	public boolean isDefused()
	{
		return defused;
	}
	
	public boolean isRedMine()
	{
		return redMine;
	}
}
