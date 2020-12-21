package labs.spring.hw7.repositories;

import labs.spring.hw7.entities.Pictures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PictureRepository extends JpaRepository<Pictures, Long> {

    List<Pictures> findAllByItem_Id(Long id);

}
