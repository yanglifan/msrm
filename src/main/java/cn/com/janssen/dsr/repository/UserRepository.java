package cn.com.janssen.dsr.repository;

import cn.com.janssen.dsr.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, String> {
    User findByEmail(String username);

    Page<User> findAllByManager(User manager, Pageable pageable);
}
