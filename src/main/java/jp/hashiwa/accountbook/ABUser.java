package jp.hashiwa.accountbook;

import java.util.Arrays;
import java.util.List;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Entity
@Table(name="user_info")
public class ABUser implements UserDetails {

  public static final Collection<? extends GrantedAuthority> authorities = Arrays.asList(
      new SimpleGrantedAuthority("USER")
    );

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private long id;

  @Column(name="username")
  private String username;

  @Column(name="password")
  private String password;

  @Column(name="role")
  private String role;

  @Column(name="enabled")
  private boolean enabled;

  public ABUser() {}
  public ABUser(String username) {
    this.username = username;
    this.enabled = true;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public boolean isEnabled() { return enabled; }
  public boolean isCredentialsNonExpired() { return true; }
  public boolean isAccountNonLocked() { return true; }
  public boolean isAccountNonExpired() { return true; }
}
