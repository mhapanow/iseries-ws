package org.dcm.services.exception;

import java.util.List;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

public class ExceptionHelper {

	static public boolean containCausalChain(Throwable searchIn, Class<?> lookfor) {
		List<Throwable> ths = Throwables.getCausalChain(searchIn);
		Iterable<?> iterables = Iterables.filter(ths, lookfor); 
		if (iterables == null) {
			return false;
		}
		for (@SuppressWarnings("unused") Object object : iterables) {
			return true;
		}
		return false;
	}
}
