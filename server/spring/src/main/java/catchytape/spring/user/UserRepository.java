package catchytape.spring.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = "select * from user u where u.user_email = :email", nativeQuery = true)
    User findByUserEmail(@Param("email") String email);
}
