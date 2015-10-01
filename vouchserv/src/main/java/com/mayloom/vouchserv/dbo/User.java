package com.mayloom.vouchserv.dbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="\"USER\"")
@NamedQueries({
	@NamedQuery(
			name=User.GET_USER_COUNT_QUERY,
			query="SELECT COUNT(user.username) FROM User user WHERE user.username = :username"
		),
	@NamedQuery(
				name=User.GET_USER_PASSWORD_COUNT_QUERY,
				query="SELECT COUNT(user.username) FROM User user WHERE user.username = :username AND user.password = :password"
			),
	@NamedQuery(
			name=User.GET_USER_QUERY,
			query="SELECT user FROM User user WHERE user.username = :username"
		),
	@NamedQuery(
			name=User.GET_USER_COUNT_WITH_ASSOCIATED_VBO_CODE_QUERY,
			query="SELECT COUNT(user.username) from User user inner join user.voucherBatchOwners vbo WHERE user.username = :username AND vbo.code IN (:vsvId)"
	)
})
public class User implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	
	public static final String GET_USER_COUNT_QUERY = "User.GetUserCount";
	public static final String GET_USER_PASSWORD_COUNT_QUERY = "User.GetUserPasswordCount";
	public static final String GET_USER_QUERY = "User.GetUser";
	public static final String GET_USER_COUNT_WITH_ASSOCIATED_VBO_CODE_QUERY = "User.GetUserCountWithAssociatedVoucherBatchOwnerCode";

	@Id
	@Column(name="USERNAME", nullable=false, unique=true)
	private String username;
	
	@Column(name="PASSWORD", nullable=false)
	private String password;
	
	@Column(name="ACCNT_NON_EXP", nullable=false)
	private boolean accountNonExpired = true;
	
	@Column(name="ACCNT_NON_LOCK", nullable=false)
	private boolean accountNonLocked = true;
	
	@Column(name="CRED_NON_EXP", nullable=false)
	private boolean credentialsNonExpired = true;
	
	@Column(name="ENABLED", nullable=false)
	private boolean enabled = true;
	
	@ManyToMany(cascade=CascadeType.PERSIST)
	@JoinTable
	(
	      name="\"USER_ROLE\"",
	      joinColumns={ @JoinColumn(name="USER_ID", referencedColumnName="USERNAME") },
	      inverseJoinColumns={ @JoinColumn(name="ROLE_ID", referencedColumnName="ID") },
	      uniqueConstraints = {
	    		    @UniqueConstraint(columnNames = {
	    		        "USER_ID",
	    		        "ROLE_ID"
	    		    })}

	 )
	private List<Role> roles = new ArrayList<Role>();
	
	// One User associated with Many VoucherBatchOwners
	@OneToMany(mappedBy="user", cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	@Fetch(FetchMode.JOIN)
	private List<VoucherBatchOwner> voucherBatchOwners = new ArrayList<VoucherBatchOwner>();

	@Override
    public Collection<GrantedAuthority> getAuthorities() {

            Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
            for (Role role : roles) {
                    grantedAuthorities.add(new GrantedAuthorityImpl(role.getAuthority()));
            }
            return grantedAuthorities;
    }

	@Override
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	public void addRole(Role role) {
		getRoles().add(role);
	}
	
	@Override
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.accountNonExpired;
	}
	
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.accountNonLocked;
	}
	
	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.credentialsNonExpired;
	}
	
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public List<VoucherBatchOwner> getVoucherBatchOwners() {
		return voucherBatchOwners;
	}

	public void setVoucherBatchOwners(List<VoucherBatchOwner> voucherBatchOwners) {
		this.voucherBatchOwners = voucherBatchOwners;
	}
}
