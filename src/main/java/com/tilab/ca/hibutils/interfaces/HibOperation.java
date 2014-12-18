package com.tilab.ca.hibutils.interfaces;

import org.hibernate.Session;

public interface HibOperation<T> {
	public T execute(Session session) throws Exception;	
}
