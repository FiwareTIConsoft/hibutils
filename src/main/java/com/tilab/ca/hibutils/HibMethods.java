package com.tilab.ca.hibutils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;



import com.tilab.ca.hibutils.interfaces.HibListOperation;
import com.tilab.ca.hibutils.interfaces.HibOperation;
import com.tilab.ca.hibutils.interfaces.HibVoidOperation;
import com.tilab.ca.hibutils.utils.Utils;

public class HibMethods {

	public static final class Constants{
		public static final int SAVE=0;
		public static final int UPDATE=1;
		public static final int SAVE_OR_UPDATE=2;
		public static final int DELETE=3;
	}
	
	/*
	 * Esegue l'operazione contenuta nel methodo execute dell'oggetto HibOperation passato come parametro
	 * Si occupa di aprire/chiudere la sessione
	 * 
	 */
	public static <T> T getOperation(SessionFactory sessFact,HibOperation<T> operation) throws Exception{
		Session session = null;
		T ret=null;
		try {
			session = sessFact.openSession();
			session.beginTransaction();
			
			ret=operation.execute(session);
			
			session.getTransaction().commit();
			
			return ret;
		} catch (HibernateException he) {
			throw he;
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}
	
	/*
	 * Esegue l'operazione contenuta nel methodo execute dell'oggetto HibOperation passato come parametro
	 * Si occupa di aprire/chiudere la sessione e di mappare l'eccezione HibernateException con una StvDaoException
	 * 
	 */
	public static <T> List<T> getListOperation(SessionFactory sessFact,HibListOperation<T> operation) throws Exception{
		Session session = null;
		List<T> retList=null;
		
		try {
			session = sessFact.openSession();
			session.beginTransaction();
			
			retList=operation.execute(session);
			
			session.getTransaction().commit();
			
			return retList;
			
		} catch (HibernateException he) {
			throw he;
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}
	
	/*
	 * Crea il criteria a partire dai criterion e la hibclass passati come parametro
	 */
	public static <T> Criteria createCriteria(Class<T> hibClass, Session session,
			List<Criterion> criterias) {
		Criteria crit = session.createCriteria(hibClass);
		
		if(Utils.isNotNullOrEmpty(criterias))
			criterias.forEach((criterion) -> crit.add(criterion));
		
		return crit;
	}
	
	
	public static <T> T getHibclassObj(Class<T> hibClass, List<String[]> paramsName,
			Object[] col){
		
		T hibClassObj;
		Class<?>[] params=new Class[1];
		Method method;
		Field f=null;
		try{
			hibClassObj=hibClass.newInstance();
			String paramName=null;
			for(int i=0;i<paramsName.size();i++){
				try{
					paramName=paramsName.get(i)[0];
					f=hibClass.getDeclaredField(paramName);
					params[0]=f.getType();
					method=hibClass.getDeclaredMethod(paramsName.get(i)[1],params);
					method.invoke(hibClassObj, col[i]);
				}catch(Exception e){
					Logger.getLogger(HibMethods.class).warn(String.format("field %s not found",paramName));
				}
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		return hibClassObj;
	}
	
	public static List<String[]> fromParList2ParName(ProjectionList plist) {
		List<String[]> paramsName=new ArrayList<String[]>();
		String propName=null;
		String[] spltName=null;
		String[] props=null;
		for(int i=0;i<plist.getLength();i++){
			props=new String[2];
			propName=plist.getProjection(i).toString();
			spltName=propName.split(" ");
			if(spltName.length>1)
				propName=spltName[spltName.length-1];
			props[0]=propName;
			props[1]="set"+propName.substring(0, 1).toUpperCase() + propName.substring(1);
			paramsName.add(props);
		}
		return paramsName;
	}
	
	
	public static void hibExecuteVoidOperation(SessionFactory sessFact,HibVoidOperation operation) throws Exception{
		
		Session session = null;
		
		try {
			session = sessFact.openSession();
			session.beginTransaction();
			
			operation.execute(session);
			
			session.getTransaction().commit();
		} catch (HibernateException he) {
			if(session!=null && session.isOpen() 
					&& session.getTransaction()!=null
					&& session.getTransaction().isActive())
				session.getTransaction().rollback();
			
			throw he;
			
		} finally {
			if (session != null && session.isOpen())
				session.close();
		}
	}
	
	public static void hibSaveUpdateOrDelete(SessionFactory sessFact,
											final int action,
											final Object... hibObjects) throws Exception{
		
		if(hibObjects==null)
			throw new IllegalArgumentException("You must pass at least one object to save or update.");
		
		hibExecuteVoidOperation(sessFact, (session) -> executeDBAction(session, action, hibObjects));
	}
	
	
	public static void executeDBAction(Session session,int action,Object... hibObjects){
		switch (action) {
		case Constants.SAVE:
			for(Object hibObj:hibObjects)
				session.save(hibObj);
			break;
		case Constants.UPDATE:
			for(Object hibObj:hibObjects)
				session.update(hibObj);
			break;
		case Constants.SAVE_OR_UPDATE:
			for(Object hibObj:hibObjects)
				session.saveOrUpdate(hibObj);
			break;
		case Constants.DELETE:
			for(Object hibObj:hibObjects)
				session.delete(hibObj);
			break;
		default:
			break;
		}
	}
	
}
