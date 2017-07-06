package io.github.erikliberal;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import lombok.extern.java.Log;

interface Repository<TipoObj> {

	TipoObj add(TipoObj e);

	TestEntity retrieve(Serializable id);

	long size();

	Collection<? extends TipoObj> retrieve();

	boolean removeAll(Collection<? extends TipoObj> c);

	boolean remove(TipoObj o);

	boolean isEmpty();

	boolean containsAll(Collection<? extends TipoObj> c);

	boolean contains(TipoObj o);

	Collection<? extends TipoObj> addAll(Collection<? extends TipoObj> c);
}

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Log
public class TestEntityRepository implements Repository<TestEntity> {

	@PersistenceContext(name = "ExampleDS")
	private EntityManager em;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public TestEntity add(TestEntity e) {
		childrenOf(e.getId()).stream().forEach(this::updatePath);
		
		return em.merge(e);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean remove(TestEntity o) {
		boolean removed = false;
		try {
			em.remove(o);
			removed = true;
		} catch (IllegalArgumentException | TransactionRequiredException e) {
			log.fine(e.getMessage());
		}
		return removed;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean removeAll(Collection<? extends TestEntity> c) {
		return c.stream().allMatch(this::remove);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Collection<? extends TestEntity> addAll(Collection<? extends TestEntity> c) {
		return c.stream().map(this::add).collect(Collectors.toList());
	}

	@Override
	public boolean isEmpty() {
		return size() != 0L;
	}

	@Override
	public long size() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		return em.createQuery(cq.select(cb.count(cq.from(TestEntity.class).get(TestEntity_.id)))).getSingleResult();
	}

	@Override
	public boolean containsAll(Collection<? extends TestEntity> c) {
		return c.stream().allMatch(this::contains);
	}

	@Override
	public boolean contains(TestEntity o) {
		return em.contains(o);
	}

	@Override
	public Collection<? extends TestEntity> retrieve() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestEntity> cq = cb.createQuery(TestEntity.class);
		return em.createQuery(cq.select(cq.from(TestEntity.class))).getResultList();
	}

	@Override
	public TestEntity retrieve(Serializable pk) {
		return em.find(TestEntity.class, pk);
	}
	
	public Collection<? extends TestEntity> childrenOf(Serializable pk) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestEntity> cq = cb.createQuery(TestEntity.class);
		Root<TestEntity> entity = cq.from(TestEntity.class);
		
		Join<TestEntity,TestEntity> parent = entity.join(TestEntity_.parent, JoinType.INNER);
		cq=cq.select(entity).where(cb.equal(parent.get(TestEntity_.id), pk));
		return em.createQuery(cq).getResultList();
	}

	
	void updatePath(TestEntity entity){
		entity.setPath(entity.getResolvedPath());
		add(entity);
	}

	@PrePersist
	@PreUpdate
	void beforePersist(TestEntity entity) {
		entity.setPath(entity.getResolvedPath());
	}
	
	@PostUpdate
	void afterUpdate(TestEntity entity){
	}

}
