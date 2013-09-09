package edu.elon.honors.price.data.field;

import java.util.List;

import edu.elon.honors.price.data.field.DataObject.Constructor;

public class FieldData extends PersistData {
	
	private static FieldData leftSide = new FieldData(), rightSide = new FieldData();

	public static class ParseDataException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public ParseDataException(String string) {
			super(string);
		}
		
		public ParseDataException() {
			super();
		}
	}
	
	public static int hashCode(DataObject object) {
		leftSide.reset();
		leftSide.dataObject = object;
		leftSide.writeMode = true;
		return leftSide.hashCode();
	}

	public static boolean areEqual(DataObject data1, DataObject data2) {
		if (data1 == data2) return true;
		if (data1 == null || data2 == null) return false;
		if (data1.getClass() != data2.getClass()) return false;
		leftSide.reset();
		leftSide.dataObject = data1;
		leftSide.writeMode = true;
		rightSide.reset();
		rightSide.dataObject = data2;
		rightSide.writeMode = true;
		return leftSide.equals(data2);
	}
	
	/** 
	 * Persists the given objects as a String in {@link Storage}, under
	 * the given identifier tag.
	 */
	public static void persistData(DataObject persistable, String tag) {
		FieldData data = new FieldData();
		data.dataObject = persistable;
		data.writeMode = true;
		data.dataMode = true;
		try {
			persistable.addFields(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 
	 * Fetches the given object from {@link Storage}, using
	 * the given identifier tag. Returns null if fetching fails.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DataObject> T fetchData(Class<T> clazz, String tag) {
		try {
			DataObject obj = Constructor.construct(clazz.getName());
			FieldData data = new FieldData();
			data.dataObject = obj;
			data.writeMode = false;
			data.dataMode = true;
			obj.addFields(data);
			return (T) obj;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return null;
	}


	@Override
	protected void addFields(DataObject dataObject) throws NumberFormatException, ParseDataException {
		dataObject.addFields(this);
	}
	
	public int add(int x) throws ParseDataException, NumberFormatException {
		addHash(x);
		return persist(x);
	}
	
	public long add(long x) throws ParseDataException, NumberFormatException {
		addHash(x);
		return persist(x);
	}
	
	public short add(short x) throws ParseDataException, NumberFormatException {
		addHash(x);
		return persist(x);
	}
	
	public float add(float x) throws ParseDataException, NumberFormatException {
		addHash(x);
		return persist(x);
	}
	
	public double add(double x) throws ParseDataException, NumberFormatException {
		addHash(x);
		return persist(x);
	}
	
	public byte add(byte x) throws ParseDataException, NumberFormatException {
		addHash(x);
		return persist(x);
	}
	
	public char add(char x) throws ParseDataException { 
		addHash(x);
		return persist(x);
	}
	
	public boolean add(boolean x) throws ParseDataException {
		addHash(x);
		return persist(x);
	}
	
	public String add(String x) throws ParseDataException {
		addHash(x);
		return persist(x);
	}
	
	public <T extends DataObject> T add(T x, Class<? extends T> clazz) throws ParseDataException, NumberFormatException {
		addHash(x);
		return persist(x, clazz);
	}
	
	public <T extends DataObject> T add(T x) throws ParseDataException, NumberFormatException {
		addHash(x);
		return persist(x);
	}
	
	public <T extends DataObject> T add(T x, String clazz) throws ParseDataException, NumberFormatException {
		addHash(x);
		return persist(x);
	}
	
	// Arrays are stored int the format "length \n 1|2|3|4"	
	public boolean[] addArray(boolean[] x) throws NumberFormatException, ParseDataException {
		addHash(x);
		return persistArray(x);
	}
	
	public int[] addArray(int[] x) throws NumberFormatException, ParseDataException {
		addHash(x);
		return persistArray(x);
	}
	
	public String[] addArray(String[] x) throws NumberFormatException, ParseDataException {
		addHash(x);
		return persistArray(x);
	}
	
	public int[][] add2DArray(int[][] x) throws NumberFormatException, ParseDataException {
		addHash(x);
		return persist2DArray(x);
	}
	
	public <T extends DataObject> T[] addArray(T[] x) throws NumberFormatException, ParseDataException {
		addHash(x);
		return persistArray(x);
	}
	
	public <T extends DataObject, L extends List<T>> L addList(L x) throws NumberFormatException, ParseDataException {
		addHash(x);
		return persistList(x);
	}
	
	/** Persists the list, using the provided class for reconstruction. See {@link Data#persist(Persistable, Class)} */
	public <T extends DataObject, L extends List<T>> L addList(L x, Class<? extends T> clazz) throws NumberFormatException, ParseDataException {
		addHash(x);
		return persistList(x);
	}
	
	public List<Integer> addIntList(List<Integer> x) throws NumberFormatException, ParseDataException  {
		addHash(x);
		return persistIntList(x);
	}

	public List<String> addStringList(List<String> x) throws NumberFormatException, ParseDataException {
		addHash(x);
		return persistStringList(x);
	}
	
	public List<Boolean> addBooleanList(List<Boolean> x) throws NumberFormatException, ParseDataException {
		addHash(x);
		return persistBooleanList(x);
	}
}
