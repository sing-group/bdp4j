package org.bdp4j.util;

import java.io.Serializable;

/**
 * A pair of generic objects implemented by using Java Generics
 * @since JDK 1.5
 * @author José Ramón Méndez
 * Implementa un par de objetos
 * @param <T1> Tipo del primer objeto
 * @param <T2> Tipo del segundo objeto
 */
public final class Pair<T1,T2> implements Serializable {
	
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 3349516261232499121L;

	/**
	 * Constructor a partir del par
	 * @param obj1 El primer objeto
	 * @param obj2 El segundo objeto
	 */
	public Pair(T1 obj1, T2 obj2){
		this.obj1=obj1;
		this.obj2=obj2;
	}
	
	/**
	 * Objeto 1
	 */
	T1 obj1;
	
	/**
	 * Objeto 2
	 */
	T2 obj2;
	
	/**
	 * @return Returns the obj1.
	 */
	public T1 getObj1() {
		return obj1;
	}
	
	/**
	 * @param obj1 The obj1 to set.
	 */
	public void setObj1(T1 obj1) {
		this.obj1 = obj1;
	}
	
	/**
	 * @return Returns the obj2.
	 */
	public T2 getObj2() {
		return obj2;
	}
	
	/**
	 * @param obj2 The obj2 to set.
	 */
	public void setObj2(T2 obj2) {
		this.obj2 = obj2;
	}
}
