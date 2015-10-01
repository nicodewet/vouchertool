package com.mayloom.vouchserv.dbo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;

@Entity
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="\"ROLE\"")
public class Role implements GrantedAuthority {
	
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)	
	@Column(name="ID", nullable=false, unique=true)
	private long id;
	
	@Column(name="AUTHORITY", nullable=false)
	private String authority;
	
	@ManyToMany(mappedBy="roles", fetch=FetchType.LAZY)
	private Set<User> users = new HashSet<User>();

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public long getId() {
		return id;
	}

	@Override
	public String getAuthority() {
		return authority;
	}
}
