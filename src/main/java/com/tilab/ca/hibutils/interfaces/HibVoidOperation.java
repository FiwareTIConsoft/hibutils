package com.tilab.ca.hibutils.interfaces;


import org.hibernate.Session;

public interface HibVoidOperation {

	public void execute(Session session) throws Exception;
}
