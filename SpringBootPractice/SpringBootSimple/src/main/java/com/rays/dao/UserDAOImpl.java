package com.rays.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.rays.dao.UserDAOInt;
import com.rays.dto.UserDTO;

@Repository
public class UserDAOImpl implements UserDAOInt {

	@PersistenceContext
	public EntityManager entityManager;

	@Override
	public long add(UserDTO dto) {
		entityManager.persist(dto);
		return dto.getId();
	}

	@Override
	public void update(UserDTO dto) {
		entityManager.merge(dto);
	}

	@Override
	public void delete(UserDTO dto) {
		entityManager.remove(dto);

	}

	@Override
	public UserDTO findByPk(long pk) {
		UserDTO dto = entityManager.find(UserDTO.class, pk);
		return dto;
	}

	@Override
	public UserDTO findByUniqueKey(String attribute, String value) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserDTO> cq = builder.createQuery(UserDTO.class);
		Root<UserDTO> qRoot = cq.from(UserDTO.class);
		Predicate condition = builder.equal(qRoot.get(attribute), value);
		cq.where(condition);

		TypedQuery<UserDTO> tq = entityManager.createQuery(cq);
		List<UserDTO> list = tq.getResultList();

		UserDTO dto = null;

		if (list.size() > 0) {
			dto = list.get(0);
		}
		return dto;
	}

	@Override
	public List search(UserDTO dto, int pageNo, int pageSize) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserDTO> cq = builder.createQuery(UserDTO.class);
		Root<UserDTO> qRoot = cq.from(UserDTO.class);

		List<Predicate> predicateList = new ArrayList<Predicate>();

		if (dto != null) {
			if (dto.getFirstName() != null && dto.getFirstName().length() > 0) {
				predicateList.add(builder.like(qRoot.get("firstName"), dto.getFirstName() + "%"));
			}
			if (dto.getId() != null && dto.getId() > 0) {
				predicateList.add(builder.equal(qRoot.get("id"), dto.getId()));
			}

		}
		cq.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<UserDTO> tq = entityManager.createQuery(cq);

		if (pageSize > 0) {
			tq.setFirstResult(pageNo * pageSize);
			tq.setMaxResults(pageSize);
		}

		List<UserDTO> list = tq.getResultList();

		return list;
	}

}
