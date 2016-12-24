package jp.hashiwa.accountbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ABUserDetailsService implements UserDetailsService {

  @Autowired
  ABUserRepository userRepo;

  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException
  {
    if (userRepo != null) {
      UserDetails user = userRepo.findByUsername(username);
      if (user != null) {
        return user;
      }
    }
    throw new UsernameNotFoundException(username);
  }

}

