package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.eujeux.data.GameInfo;

import edu.elon.honors.price.game.Debug;

import android.content.Context;
import android.provider.Settings.Secure;

public class GameCache implements Serializable {
	private static final long serialVersionUID = 2L;
	private static final String FILE_NAME = "gameCache";
	
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
	
	public Iterable<GameDetails> getGames(GameType type) {
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
		String[] files = context.fileList();
		int n = 0;
		String name;
		boolean exists = false;
		do {
			name = base + ++n;
			exists = false;
			for (int i = 0; i < files.length; i++) 
				exists |= files[i].equals(name); 
		} while (exists);
		return name;
	}
	
	private void save(Context context) {
		Data.saveData(FILE_NAME, context, this);
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
			getGamesList(type).add(details);
			save(context);
			return details;
		} else {
			Debug.write("Error saving game %s", name);
			return null;
		}
	}
	
	public boolean updateGame(GameDetails details, PlatformGame game, 
			GameInfo websiteInfo, Context context) {
		if (Data.saveData(details.filename, context, game)) {
			if (websiteInfo != null) details.name = websiteInfo.name;
			details.websiteInfo = websiteInfo;
			details.lastEdited = websiteInfo.lastEdited;
			save(context);
			return true;
		}
		return false;
	}
	
	public void deleteGame(GameDetails details, GameType type, Context context) {
		context.deleteFile(details.filename);
		getGamesList(type).remove(details);
		save(context);
	}

	private GameCache() { }
	
	@SuppressWarnings("deprecation")
	public static GameCache getGameCache(Context context) {
		if (instance != null) return instance;
		
		GameCache cache = Data.loadData(FILE_NAME, context);
		
		if (cache != null) return cache;
		
		context.deleteFile(FILE_NAME);
		
		cache = new GameCache();
		String[] files = context.fileList();
		for (String file : files) {
			PlatformGame game = (PlatformGame)Data.loadData(file, context);
			if (game != null) {
				GameDetails details = new GameDetails(game.getName("Map"), file, 
						game.websiteInfo);
				cache.myGames.add(details);
			}
		}
		
		Data.saveData(FILE_NAME, context, cache);
		instance = cache;
		
		return cache;
	}
	
	public static class GameDetails implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public String name;
		public String filename;	
		public Date dateCreated;
		public GameInfo websiteInfo;
		public long lastEdited;
		
		public boolean hasWebsiteInfo() {
			return websiteInfo != null;
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
		
		public PlatformGame loadGame(Context Context) {
			return (PlatformGame)Data.loadData(filename, Context);
		}
		
		@Override
		public String toString() {
			return String.format("%s [%s]; %s", name, filename, 
					dateCreated.toLocaleString());
		}

		public boolean hasWebisiteId(long id) {
			return websiteInfo != null && websiteInfo.id == id;
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
