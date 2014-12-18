package com.tilab.ca.hibutils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;

import com.tilab.ca.hibutils.interfaces.HibListOperation;
import com.tilab.ca.hibutils.interfaces.HibOperation;
import com.tilab.ca.hibutils.interfaces.HibVoidOperation;
import com.tilab.ca.hibutils.utils.Utils;

public class Hibutils {
	
	@SuppressWarnings("unchecked")
	public static <T> T getSingleObj(SessionFactory sessFact,
							  Class<?> hibClass,
							  List<Criterion> criterionList,
							  Integer maxResults,
							  Integer firstResult,
							  Order order,
							  ProjectionList plist,
							  Class<?> retClass) throws Exception{
		
		return HibMethods.getOperation(sessFact, (session) -> {

		
				Criteria crit = HibMethods.createCriteria(hibClass, session,criterionList);
				
				if(maxResults!=null)
					crit.setMaxResults(maxResults);
				
				if(firstResult!=null)
					crit.setMaxResults(firstResult);
					
				if(order!=null)
					crit.addOrder(order);
				
				if(plist!=null)
					return (T) handleProjection(session, retClass,crit, plist);
				
				return (T) crit.uniqueResult();
			});
	}
	
	
	public static <T> T getOperation(SessionFactory sessFact,HibOperation<T> operation) throws Exception{
		return HibMethods.getOperation(sessFact, operation);
	}
	
	public static <T> List<T> getListOperation(SessionFactory sessFact,HibListOperation<T> operation) throws Exception{
		return HibMethods.getListOperation(sessFact, operation);
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> getObjList(final SessionFactory sessFact,
			 Class<?> hibClass,
			List<Criterion> criterionList,
			  Integer maxResults,
			  Integer firstResult,
			  Order order,
			  ProjectionList plist,
			  Class<?> retClass) throws Exception{
		
		return HibMethods.getListOperation(sessFact, (session) -> {

		
			Criteria crit = HibMethods.createCriteria(hibClass, session,criterionList);
			
			if(maxResults!=null)
				crit.setMaxResults(maxResults);
			
			if(firstResult!=null)
				crit.setFirstResult(firstResult);
				
			if(order!=null)
				crit.addOrder(order);
				
			if(plist!=null)
					return  (List<T>) handleProjectionList(session, retClass,
												crit, plist);
				
 				return (List<T>)crit.list();
			});
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> handleProjectionList(Session session,
			Class<T> hibClass,Criteria crit,ProjectionList plist) throws Exception{
		
		crit.setProjection(plist);
		
		if(Utils.isPrimitiveWrappingClass(hibClass) || hibClass.equals(String.class))
			return (List<T>)crit.list();
		
		List<String[]> paramsName = HibMethods.fromParList2ParName(plist);
		
		List<Object> objList=crit.list();
		List<T> retList=new LinkedList<T>();
		
		if(Utils.isNullOrEmpty(objList))
			return null;
		
		objList.forEach((o) -> retList.add(HibMethods.getHibclassObj(hibClass, paramsName, (Object[])o)));
		
		return retList;
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> T handleProjection(Session session,Class<T> hibClass,Criteria crit,ProjectionList plist) throws Exception{
		
		crit.setProjection(plist);
		
		if(hibClass.isPrimitive() || hibClass.equals(String.class))
			return (T)crit.uniqueResult();
		
		List<String[]> paramsName = HibMethods.fromParList2ParName(plist);
		
		Object[] col=(Object[])crit.uniqueResult();
		
		if(col==null)
			return null;
		
		return HibMethods.getHibclassObj(hibClass, paramsName, col);

	}
	
	/*
	 * Si occupa di aprire la sessione e salvare l'oggetto loggando l'eventuale eccezione
	 */
	public static void save(SessionFactory sessFact,Object... hibObjects) throws Exception{
		HibMethods.hibSaveUpdateOrDelete(sessFact, HibMethods.Constants.SAVE, hibObjects);
	}
	
	public static void save(SessionFactory sessFact,Collection<?> hibObjColl) throws Exception{
		HibMethods.hibSaveUpdateOrDelete(sessFact, HibMethods.Constants.SAVE, hibObjColl.toArray());
	}
	
	public static void update(SessionFactory sessFact,Object... hibObjects) throws Exception{
		HibMethods.hibSaveUpdateOrDelete(sessFact, HibMethods.Constants.UPDATE, hibObjects);
	}
	
	public static void update(SessionFactory sessFact,Collection<?> hibObjColl) throws Exception{
		HibMethods.hibSaveUpdateOrDelete(sessFact, HibMethods.Constants.UPDATE, hibObjColl.toArray());
	}
	
	public static void saveOrUpdate(SessionFactory sessFact,Object... hibObjects) throws Exception{
		HibMethods.hibSaveUpdateOrDelete(sessFact, HibMethods.Constants.SAVE_OR_UPDATE, hibObjects);
	}
	
	public static void saveOrUpdate(SessionFactory sessFact,Collection<?> hibObjColl) throws Exception{
		HibMethods.hibSaveUpdateOrDelete(sessFact, HibMethods.Constants.SAVE_OR_UPDATE, hibObjColl.toArray());
	}
	
	public static void delete(SessionFactory sessFact,Object... hibObjects) throws Exception{
		HibMethods.hibSaveUpdateOrDelete(sessFact, HibMethods.Constants.DELETE, hibObjects);
	}
	
	public static void delete(SessionFactory sessFact,Collection<?> hibObjColl) throws Exception{
		HibMethods.hibSaveUpdateOrDelete(sessFact, HibMethods.Constants.DELETE, hibObjColl.toArray());
	}
	
	public static void executeVoidOperation(SessionFactory sessFact,HibVoidOperation operation) throws Exception{
		HibMethods.hibExecuteVoidOperation(sessFact, operation);
	}
	
	
}
