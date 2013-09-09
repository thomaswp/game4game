package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.provider.Settings.Secure;

import com.eujeux.data.GameInfo;

import edu.elon.honors.price.data.tutorial.Tutorial;
import edu.elon.honors.price.game.Debug;

public class GameCache implements Serializable {
	private static final long serialVersionUID = 2L;
	private static final String FILE_NAME = "gameCache";
	
	private int tutorialVersion;
	
	private static GameCache instance;
	
	public enum GameType { Edit, Play, Tutorial }
	
	private LinkedList<GameDetails> myGames = new LinkedList<GameDetails>(),
			downloadedGames = new LinkedList<GameDetails>(),
			tutorials = new LinkedList<GameDetails>();
	
	public Iterable<GameDetails> getGames(EnumSet<GameType> types) {
		LinkedList<Iterable<GameDetails>> lists = 
				new LinkedList<Iterable<GameDetails>>();
		for (GameType t : types) {
			lists.add(getGamesList(t));
		}
		return new CompoundIterable<GameDetails>(lists);
	}
	
	public boolean tutorialsUpToDate() {
		return tutorialVersion == Tutorial.VERSION;
	}
	
	public void updateTutorialVersion(Context context) {
		this.tutorialVersion = Tutorial.VERSION;
		save(context);
	}
	
	//public Iterable<GameDetails> getGames(GameType type) {
	public List<GameDetails> getGames(GameType type) {
		return getGamesList(type);
	}
	
	private List<GameDetails> getGamesList(GameType type) {
		switch (type) {
		case Play: return downloadedGames;
		case Tutorial: return tutorials;
		default: return myGames;
		}
	}
	
	private String getNewGameID(Context context) {
		String id = Secure.getString(context.getContentResolver(), 
				Secure.ANDROID_ID);
		id += "_" + System.currentTimeMillis();
		return id;
	}
	
	private String getNewFilename(String base, Context context) {
		base = base.replaceAll("[^A-Za-z0-9_]", "");
		if (base.length() == 0) base = "Map";
		if (base.length() > 10) base = base.substring(0, 10);
		String[] files = context.fileList();
		int n = 0;
		String name = base;
		boolean exists = false;
		do {
			exists = false;
			for (int i = 0; i < files.length; i++) 
				exists |= files[i].equals(name);
			if (!exists) break;
			name = base + ++n;
		} while (true);
		return name;
	}
	
	private void save(Context context) {
		Debug.write("start save cache");
		Data.saveData(FILE_NAME, context, this);
		Debug.write("end save cache");
	}
	
	public GameDetails addGame(String name, GameType type, PlatformGame game, 
			Context context) {
		return addGame(name, type, game, null, context);
	}
	
	public GameDetails addGame(String name, GameType type, PlatformGame game, 
			GameInfo websiteInfo, Context context) {
		String filename = getNewFilename(name, context);
		game.ID = getNewGameID(context);
		if (Data.saveData(filename, context, game)) {
			GameDetails details = new GameDetails(name, filename, websiteInfo);
			details.tutorial = (Tutorial) game.tutorial;
			getGamesList(type).add(details);
			save(context);
			return details;
		} else {
			Debug.write("Error saving game %s", name);
			return null;
		}
	}
	
	public boolean updateGame(GameDetails details, PlatformGame game, 
			Context context) {
		return updateGame(details, game, null, context);
	}
	
	public boolean updateGame(GameDetails details, PlatformGame game, 
			GameInfo websiteInfo, Context context) {
		if (Data.saveData(details.filename, context, game)) {
			details.tutorial = (Tutorial) game.tutorial;
			return updateGame(details, websiteInfo, context);
		}
		return false;
	}
	
	public boolean updateGame(GameDetails details, GameInfo websiteInfo, 
			Context context) {
		if (websiteInfo != null) {
			details.name = websiteInfo.name;
			details.websiteInfo = websiteInfo;
			details.lastEdited = websiteInfo.lastEdited;
		}
		save(context);
		return true;
	}
	
	public void deleteGame(GameDetails details, GameType type, Context context) {
		boolean found = false;
		for (GameDetails d : getGamesList(type)) {
			if (d.filename.equals(details.filename)) {
				found = true;
				break;
			}
		}
		
		if (!found) {
			Debug.write("No game with filename %s for type %s", 
					details.filename, type.toString());
			return;
		}
		
		context.deleteFile(details.filename);
		getGamesList(type).remove(details);
		save(context);
	}

	private GameCache() { }
	
	@SuppressWarnings("deprecation")
	public static GameCache getGameCache(Context context) {
		if (instance != null) {
			Debug.write("load instance");
			return instance;
		}
		
		Debug.write("load cache");
		GameCache cache = Data.loadData(FILE_NAME, context);
		instance = cache;
		
		if (cache != null) return cache;
		
		// load old style of storage
		Debug.write("create cache");
		context.deleteFile(FILE_NAME);
		
		cache = new GameCache();
		String[] files = context.fileList();
		for (String file : files) {
			PlatformGame game = (PlatformGame)Data.loadData(file, context);
			if (game != null) {
				GameDetails details = new GameDetails(game.getName("Map"), file, 
						null);
				details.lastEdited = game.lastEdited;
				cache.myGames.add(details);
			}
		}
		
		Data.saveData(FILE_NAME, context, cache);
		instance = cache;
		
		return cache;
	}
	
	public GameDetails makeCopy(GameDetails details, String name, GameType type,
			Context context) {
		PlatformGame game = details.loadGame(context);
		game.stripEditorData();
		GameDetails d = addGame(name, type, game, context);
		if (details.hasWebsiteInfo()) {
			d.branchedGameId = details.websiteInfo.id;
			save(context);
		}
		return d;
	}
	
	public static class GameDetails implements Serializable {
		

		private static final long serialVersionUID = 1L;
		
		private String name;
		private String filename;	
		private Date dateCreated;
		private GameInfo websiteInfo;
		private long lastEdited;
		private long branchedGameId;
		private Tutorial tutorial;
		
		public String getName() {
			return name;
		}

		public String getFilename() {
			return filename;
		}

		public Date getDateCreated() {
			return dateCreated;
		}

		public GameInfo getWebsiteInfo() {
			return websiteInfo;
		}

		public long getLastEdited() {
			return lastEdited;
		}

		public boolean hasWebsiteInfo() {
			return websiteInfo != null;
		}
		
		public long getBranchedGameId() {
			return branchedGameId;
		}
		
		public Tutorial getTutorial() {
			return tutorial;
		}

		public GameDetails(String name, String filename, GameInfo websiteInfo) {
			this.name = name;
			this.filename = filename;
			this.websiteInfo = websiteInfo;
			if (websiteInfo != null) {
				lastEdited = websiteInfo.lastEdited;
			}
			dateCreated = new Date();
		}
		
		public PlatformGame loadGame(Context context) {
			return (PlatformGame)Data.loadData(filename, context);
		}
		
		public boolean saveGame(PlatformGame game, Context context) {
//			if (getByFilename(filename, context) != this) {
//				throw new RuntimeException("GameDetails out of sync!");
//			}
			
			GameCache gameCache = getGameCache(context);
			GameDetails details = getByFilename(filename, context);
			details.lastEdited = System.currentTimeMillis();
			return gameCache.updateGame(details, game, context);
		}
		
//		public GameDetails getSynced(Context context) {
//			return getByFilename(filename, context);
//		}
		
		@Override
		public String toString() {
			return String.format("%s [%s]; %s", name, filename, 
					dateCreated.toLocaleString());
		}

		public boolean hasWebisiteId(long id) {
			return websiteInfo != null && websiteInfo.id == id;
		}
		

		public static GameDetails getByFilename(String filename, Context context) {
			GameCache gameCache = GameCache.getGameCache(context);
			for (GameType type : GameType.values()) {
				for (GameDetails details : gameCache.getGamesList(type)) {
					if (details.filename.equals(filename)) {
						return details;
					}
				}
			}
			return null;
		}
		
//		@Override
//		public boolean equals(Object o) {
//			if (o == null) return false;
//			if (!(o instanceof GameDetails)) return false;
//			GameDetails details = (GameDetails) o;
//			return details.name.eq
//			
//		}
	}
	
	public static class CompoundIterable<T> implements Iterable<T> {

		private Iterable<Iterable<T>> iterables;
		
		public CompoundIterable(Iterable<Iterable<T>> iterables) {
			this.iterables = iterables;
		}
		
		@Override
		public Iterator<T> iterator() {
			return new CompoundIterator<T>(iterables);
		}
		
	}
	
	public static class CompoundIterator<T> implements Iterator<T> {

		private Iterator<Iterable<T>> iterators;
		private Iterator<T> currentIterator;
		
		public CompoundIterator(Iterable<Iterable<T>> iterables) {
			this.iterators = iterables.iterator();
			loadNextIterator();
		}
		
		@Override
		public boolean hasNext() {
			return currentIterator != null && currentIterator.hasNext();
		}

		@Override
		public T next() {
			T item = currentIterator.next();
			if (!currentIterator.hasNext()) {
				loadNextIterator();
			}
			return item;
		}
		
		private void loadNextIterator() {
			if (iterators.hasNext()) {
				currentIterator = iterators.next().iterator();
			} else {
				currentIterator = null;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		
	}
}
