package com.tilab.ca.hibutils.interfaces;

import java.util.List;

import org.hibernate.Session;

public interface HibListOperation<T> {

	public List<T> execute(Session session) throws Exception;
	
}
