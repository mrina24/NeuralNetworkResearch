package com.nwgjb.commons;

import java.util.Comparator;

/**
 * A representation of tuple.
 *
 */
public class Tuple {
	public static class _2<T1, T2> extends Tuple{
		
		public T1 _1;
		public T2 _2;
		
		public _2(T1 _1, T2 _2){
			this._1=_1;
			this._2=_2;
		}
		
		public static <T1 extends Comparable<T1>> Comparator<_2<T1, ?>> comp1A(){
			return new Comparator<_2<T1, ?>>(){
				@Override
				public int compare(_2<T1, ?> o1, _2<T1, ?> o2) {
					return o1._1.compareTo(o2._1);
				}
			};
		}
		
		public static <T2 extends Comparable<T2>> Comparator<_2<?, T2>> comp2A(){
			return new Comparator<_2<?, T2>>(){
				@Override
				public int compare(_2<?, T2> o1, _2<?, T2> o2) {
					return o1._2.compareTo(o2._2);
				}
			};
		}
		
		public static <T2 extends Comparable<T2>> Comparator<_2<?, T2>> comp2D(){
			return new Comparator<_2<?, T2>>(){
				@Override
				public int compare(_2<?, T2> o1, _2<?, T2> o2) {
					return -o1._2.compareTo(o2._2);
				}
			};
		}
	}
	
	public static <T1, T2> _2<T1, T2> as(T1 _1, T2 _2){
		return new _2<>(_1, _2);
	}
}
