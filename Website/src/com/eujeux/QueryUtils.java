package com.eujeux;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;


public class QueryUtils {
	
	public static <T> T queryUnique(Class<T> c, String filter, Object... parameters) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		T answer = queryUnique(pm, c, filter, parameters);
		pm.close();
		return answer;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T queryUnique(PersistenceManager pm, 
			Class<T> c, String filter, Object... parameters) {
		return (T)query(pm, c, true, filter, parameters);
	}
	
	public static <T> List<T> query(Class<T> c, String filter, Object... parameters) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<T> answer = query(pm, c, filter, parameters);
		LinkedList<T> copy = new LinkedList<T>();
		copy.addAll(answer);
		pm.close();
		return copy;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> query(PersistenceManager pm, 
			Class<T> c, String filter, Object... parameters) {
		return (List<T>)query(pm, c, false, filter, parameters);
	}

	/**
	 * 
	 * @param pm
	 * @param c
	 * @param unique
	 * @param filter Write a filter using %s as a standin for each parameter
	 * @param parameters List parameters using actual data type (not toString)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T> Object query(PersistenceManager pm, 
			Class<T> c, boolean unique, String filter, Object... parameters) {
		Object[] peqs = new String[parameters.length];
		String declaredParams = "";
		for (int i = 0; i < peqs.length; i++) {
			peqs[i] = "p" + i;
			
			if (parameters[i] == null) {
				if (unique) return null;
				else return new LinkedList<T>();
			}
			
			if (declaredParams.length() > 0) declaredParams += ", ";
			declaredParams += parameters[i].getClass().getName() + " " +
					peqs[i];
		}
		Debug.write(declaredParams);
		Debug.write(String.format(filter, peqs));
		
		Query q = pm.newQuery(c);
		q.setFilter(String.format(filter, peqs));
		q.declareParameters(declaredParams);
		q.setUnique(unique);
		
		Object result;
		
		if (parameters.length == 0) {
			result = q.execute();
		} else if (parameters.length == 1) {
			result = q.execute(parameters[0]);
			Debug.write(result);
		} else if (parameters.length == 2) {
			result = q.execute(parameters[0], parameters[1]);
		} else if (parameters.length == 3) {
			result = q.execute(parameters[0], parameters[1], parameters[2]);
		} else {
			return null;
		}
		
		if (unique) return (T)result;
		
		return (List<T>)result;
	}
}
