package edu.elon.honors.price.data.field;

import java.util.ArrayList;
import java.util.List;

import edu.elon.honors.price.data.field.DataObject.Constructor;
import edu.elon.honors.price.data.field.FieldData.ParseDataException;

abstract class PersistData extends HashData {

	private final static String NULL = "null";
	private final static String DIV = "|";
	
	protected boolean writeMode;
	protected boolean dataMode;
	
	@Override
	protected void reset() {
		super.reset();
		dataMode = false;
	}
	
	public boolean writeMode() {
		return writeMode;
	}

	public boolean readMode() {
		return !writeMode;
	}
	
	public int persist(int x) throws ParseDataException, NumberFormatException {
		if (!dataMode) return x;
		if (writeMode) {
			write(String.valueOf(x));
		} else {
			x = Integer.parseInt(read());
		}
		return x;
	}
	
	public long persist(long x) throws ParseDataException, NumberFormatException {
		if (!dataMode) return x;
		if (writeMode) {
			write(String.valueOf(x));
		} else {
			x = Integer.parseInt(read());
		}
		return x;
	}
	
	public short persist(short x) throws ParseDataException, NumberFormatException {
		if (!dataMode) return x;
		if (writeMode) {
			write(String.valueOf(x));	
		} else {
			x = Short.parseShort(read());
		}
		return x;
	}
	
	public float persist(float x) throws ParseDataException, NumberFormatException {
		if (!dataMode) return x;
		if (writeMode) {
			write(String.valueOf(x));
		} else {
			x = Float.parseFloat(read());
		}
		return x;
	}
	
	public double persist(double x) throws ParseDataException, NumberFormatException {
		if (!dataMode) return x;
		if (writeMode) {
			write(String.valueOf(x));
		} else {
			x = Double.parseDouble(read());
		}
		return x;
	}
	
	public byte persist(byte x) throws ParseDataException, NumberFormatException {
		if (!dataMode) return x;
		if (writeMode) {
			write(String.valueOf(x));
		} else {
			x = Byte.parseByte(read());
		}
		return x;
	}
	
	public char persist(char x) throws ParseDataException { 
		if (!dataMode) return x;
		if (writeMode) {
			write(String.valueOf(x));
		} else {
			String r = read();
			if (r == null || r.length() < 1) throw new ParseDataException();
			x = r.charAt(0);
		}
		return x;
	}
	
	public boolean persist(boolean x) throws ParseDataException {
		if (!dataMode) return x;
		if (writeMode) {
			write(String.valueOf(x));
		} else {
			Boolean b = Boolean.parseBoolean(read());
			if (b == null) throw new ParseDataException();
			x = b;
		}
		return x;
	}
	
	public String persist(String x) throws ParseDataException {
		if (!dataMode) return x;
		if (writeMode) {
			write(x);
		} else {
			x = read();
		}
		return x;
	}
	
	public <T extends DataObject> T persist(T x) throws ParseDataException, NumberFormatException {
		if (!dataMode) return x;
		return persist(x, (String) null);
	}
	
	public <T extends DataObject> T persist(T x, Class<? extends T> clazz) throws ParseDataException, NumberFormatException {
		return persist(x, clazz == null ? null : clazz.getName());
	}
	
	/** 
	 * Persists the given object, using the provided class to reconstruct the object. 
	 * This is useful if the item's class is an anonymous or derived class that is not
	 * registered with {@link PersistUtils}, but which does not itself have any important
	 * persistent fields.
	 */
	@SuppressWarnings("unchecked")
	public <T extends DataObject> T persist(T x, String clazz) throws ParseDataException, NumberFormatException {
		if (!dataMode) return x;
		String type;
		if (writeMode) {
			type = x == null ? NULL : x.getClass().getName();
			if (clazz != null) type = clazz;
			persist(type);
			if (x != null) addFields(x);
		} else {
			type = persist((String) null);
			if (type.equals(NULL)) {
				x = null;
			} else {
				if (x != null) {
					if (!x.getClass().getName().equals(type)) throw new ParseDataException();
				} else {
					x = (T) Constructor.construct(type);
				}
				addFields(x);
			}
		}
		System.out.println("end " + type);
		return x;
	}
	
	private interface ArrayIO {
		int length(Object x);
		String read(Object x, int index);
		void set(Object x, int index, String value);
		Object create(int length);
	}
	
	// Arrays are stored int the format "length{\n}1|2|3|4"
	private Object persistArray(Object x, ArrayIO io) throws NumberFormatException, ParseDataException {
		if (!dataMode) return x;
		if (writeMode) {
			int length = persist(x == null ? 0 : io.length(x));
			if (x != null) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < length; i++) {
					if (sb.length() != 0) sb.append(DIV);
					sb.append(io.read(x, i));
				}
				write(sb.toString());
			}
		} else {
			int length = persist(0);
			if (length != 0) {
				if (x == null || io.length(x) != length) {
					x = io.create(length);
				}
				String[] data = read().split("\\" + DIV);
				int i = 0;
				for (String part : data) {
					io.set(x, i++, part);
				}
			} else {
				x = null;
			}
		}
		return x;
	}
	
	private static ArrayIO intIO = new ArrayIO() {
		
		private int[] cast(Object x) {
			return (int[]) x;
		}
		
		@Override
		public void set(Object x, int index, String value) {
			cast(x)[index] = Integer.parseInt(value);
		}
		
		@Override
		public String read(Object x, int index) {
			return String.valueOf(cast(x)[index]);
		}
		
		@Override
		public int length(Object x) {
			return cast(x).length;
		}
		
		@Override
		public Object create(int length) {
			return new int[length];
		}
	};
	
	private static ArrayIO booleanIO = new ArrayIO() {
		
		private boolean[] cast(Object x) {
			return (boolean[]) x;
		}
		
		@Override
		public void set(Object x, int index, String value) {
			cast(x)[index] = Boolean.parseBoolean(value);
		}
		
		@Override
		public String read(Object x, int index) {
			return String.valueOf(cast(x)[index]);
		}
		
		@Override
		public int length(Object x) {
			return cast(x).length;
		}
		
		@Override
		public Object create(int length) {
			return new boolean[length];
		}
	};
	
	private static ArrayIO stringIO = new ArrayIO() {
		
		private String[] cast(Object x) {
			return (String[]) x;
		}
		
		@Override
		public void set(Object x, int index, String value) {
			cast(x)[index] = value;
		}
		
		@Override
		public String read(Object x, int index) {
			return cast(x)[index];
		}
		
		@Override
		public int length(Object x) {
			return cast(x).length;
		}
		
		@Override
		public Object create(int length) {
			return new String[length];
		}
	};
	
	
	public int[] persistArray(int[] x) throws NumberFormatException, ParseDataException {
		return (int[]) persistArray(x, intIO);
	}
	
	public boolean[] persistArray(boolean[] x) throws NumberFormatException, ParseDataException {
		return (boolean[]) persistArray(x, booleanIO);
	}
	
	public String[] persistArray(String[] x) throws NumberFormatException, ParseDataException {
		return (String[]) persistArray(x, stringIO);
	}
	
	public int[][] persist2DArray(int[][] x) throws NumberFormatException, ParseDataException {
		if (!dataMode) return x;
		if (writeMode) {
			int length = persist(x == null ? 0 : x.length);
			if (x != null) {
				for (int i = 0; i < length; i++) {
					persistArray(x[i]);
				}
			}
		} else {
			int length = persist(0);
			if (length != 0) {
				if (x == null || x.length != length){
					x = new int[length][];
				}
				for (int i = 0; i < length; i++) {
					x[i] = persistArray((int[]) null);
				}
			} else {
				x = null;
			}
		}
		return x;
	}
	
	public <T extends DataObject> T[] persistArray(T[] x) throws NumberFormatException, ParseDataException {
		if (!dataMode) return x;
		if (writeMode) {
			persist(x == null ? -1 : x.length);
			if (x != null) {
				for (T t : x) {
					persist(t);
				}
			}
		} else {
			int length = persist(0);
			if (length >= 0) {
				if (x != null){
					if (x.length != length) throw new ParseDataException("Cannot read into a different sized array");
				} else {
					throw new ParseDataException("Cannot read into a null array");
				}
				for (int i = 0; i < length; i++) {
					x[i] = persist((T) null);
				}
			} else {
				x = null;
			}
		}
		return x;
	}
	
	public <T extends DataObject, L extends List<T>> L persistList(L x) throws NumberFormatException, ParseDataException {
		if (!dataMode) return x;
		return persistList(x, null);
	}
	
	/** Persists the list, using the provided class for reconstruction. See {@link Data#persist(Persistable, Class)} */
	public <T extends DataObject, L extends List<T>> L persistList(L x, Class<? extends T> clazz) throws NumberFormatException, ParseDataException {
		if (!dataMode) return x;
		if (writeMode) {
			persist(x == null ? -1 : x.size());
			if (x != null) {
				for (T t : x) {
					persist(t);
				}
			}
		} else {
			int length = persist(0);
			if (length >= 0) {
				if (x != null){
					x.clear();
				} else {
					throw new ParseDataException("Cannot read into a null array");
				}
				for (int i = 0; i < length; i++) {
					x.add(persist((T) null, clazz));
				}
			} else {
				x = null;
			}
		}
		return x;
	}
	
	public List<Integer> persistIntList(List<Integer> x) throws NumberFormatException, ParseDataException  {
		return persistPrimitiveList(x, intIO);
	}
	
	public List<String> persistStringList(List<String> x) throws NumberFormatException, ParseDataException  {
		return persistPrimitiveList(x, stringIO);
	}
	
	public List<Boolean> persistBooleanList(List<Boolean> x) throws NumberFormatException, ParseDataException  {
		return persistPrimitiveList(x, booleanIO);
	}
	
	public <T> List<T> persistPrimitiveList(List<T> x, ArrayIO io) throws NumberFormatException, ParseDataException  {
		if (!dataMode) return x;
		if (writeMode) {
			int length = x == null ? 0 : x.size();
			Object array = io.create(length);
			for (int i = 0; i < length; i++) {
				io.set(array, i, String.valueOf(x.get(i)));
			}
			persistArray(array, io);
			return x;
		} else {
			@SuppressWarnings("unchecked")
			T[] array = (T[]) persistArray(null, io);
			if (array == null) return null;
			ArrayList<T> list = new ArrayList<T>();
			for (T i : array) list.add(i);
			return list;
		}
	}
	
	private String read() throws ParseDataException {
		return "";
	}
	
	private void write(String data) {
		System.out.println(data);
	}
}
