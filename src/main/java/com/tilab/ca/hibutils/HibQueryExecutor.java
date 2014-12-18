package com.tilab.ca.hibutils;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

public class HibQueryExecutor<T> {

	private List<Criterion> criterionList = null;
	private ProjectionList plist = null;
	private Order order = null;
	private Integer maxResults = null;
	private Integer firstResult = null;
	private Class<?> hibClass = null;
	private Class<T> retClass = null;

	public HibQueryExecutor<T> select(String... projections) {

		if (projections != null) {
			plist = Projections.projectionList();
			for (String prj : projections)
				plist.add(Projections.property(prj));
		}

		return this;
	}
	
	public HibQueryExecutor<T> selectAs(String propertyName,String alias) {

		if (StringUtils.isBlank(alias))
			throw new IllegalArgumentException(
					"sumAs cannot be setted without an alias");

		if (plist == null)
			plist = Projections.projectionList();

		plist.add(Projections.alias(Projections.property(propertyName), alias));

		return this;
	}

	public HibQueryExecutor<T> selectDistinct(String projection) {

		if (StringUtils.isNotBlank(projection)) {
			if (plist == null)
				plist = Projections.projectionList();
			plist.add(Projections.distinct(Projections.property(projection)));
		}

		return this;
	}

	public HibQueryExecutor<T> rowCountAs(String alias) {
		if (StringUtils.isBlank(alias))
			throw new IllegalArgumentException(
					"rowCountAs cannot be setted without an alias");

		if (plist == null)
			plist = Projections.projectionList();

		plist.add(Projections.alias(Projections.rowCount(), alias));

		return this;
	}
	
	public HibQueryExecutor<T> sumAs(String propertyName,String alias) {
		if (StringUtils.isBlank(alias))
			throw new IllegalArgumentException(
					"sumAs cannot be setted without an alias");

		if (plist == null)
			plist = Projections.projectionList();

		plist.add(Projections.alias(Projections.sum(propertyName), alias));

		return this;
	}

	public HibQueryExecutor<T> from(Class<?> hibClass) {
		this.hibClass = hibClass;
		return this;
	}

	public HibQueryExecutor<T> where(Criterion... criterions) {

		if (criterionList == null)
			criterionList = new LinkedList<Criterion>();

		if (criterions != null) {
			for (Criterion crt : criterions)
				criterionList.add(crt);
		}

		return this;
	}

	public HibQueryExecutor<T> and(Criterion crit) {

		if (criterionList == null)
			throw new IllegalStateException(
					"Cannot use and without where statement");

		criterionList.add(crit);

		return this;
	}

	public HibQueryExecutor<T> groupBy(String propName) {

		if (plist == null)
			throw new IllegalArgumentException("groupBy need select before");

		plist.add(Projections.groupProperty(propName));

		return this;
	}

	public HibQueryExecutor<T> orderBy(Order order) {
		this.order = order;
		return this;
	}

	public HibQueryExecutor<T> maxResults(Integer maxResults) {
		this.maxResults = maxResults;
		return this;
	}

	public HibQueryExecutor<T> firstResult(Integer firstResult) {
		this.firstResult = firstResult;
		return this;
	}

	public HibQueryExecutor<T> retClass(Class<T> retClass) {
		this.retClass = retClass;
		return this;
	}
	
	public T uniqueResult(SessionFactory sessFact) throws Exception{
		checkArgs();	
		return Hibutils.getSingleObj(sessFact, hibClass, criterionList, maxResults, 
											firstResult, order, plist, retClass);
	}
	
	public List<T> listResult(SessionFactory sessFact) throws Exception{
		checkArgs();
		return Hibutils.getObjList(sessFact, hibClass, criterionList, maxResults, firstResult, 
									order, plist, retClass);
	}

	private void checkArgs() {

		if (hibClass == null)
			throw new IllegalStateException(
					"Hibernate class for criteria not defined!");
		if (plist != null && retClass == null) {
			throw new IllegalArgumentException(
					"retclass cannot be null if projections are defined!");
		}
	}
}
