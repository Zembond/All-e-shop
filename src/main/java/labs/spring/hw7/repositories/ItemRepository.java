package labs.spring.hw7.repositories;

import labs.spring.hw7.entities.Brands;
import labs.spring.hw7.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByNameLikeOrderByPriceAsc (String name);
    List<Item> findAllByNameLikeOrderByPriceDesc (String name);
    List<Item> findAllByNameLikeAndPriceBetweenOrderByPriceAsc (String name, double price1, double price2);
    List<Item> findAllByNameLikeAndPriceBetweenOrderByPriceDesc (String name, double price1, double price2);
    List<Item> findAllByNameLikeAndBrandNameLikeAndPriceBetweenOrderByPriceAsc (String name, String brand_name, double price1, double price2);
    List<Item> findAllByNameLikeAndBrandNameLikeAndPriceBetweenOrderByPriceDesc (String name, String brand_name, double price1, double price2);
    List<Item> findAllByNameLikeAndBrandIdAndPriceBetweenOrderByPriceAsc(String name, Long brand_id, double price1, double price2);
    List<Item> findAllByNameLikeAndBrandIdAndPriceBetweenOrderByPriceDesc(String name, Long brand_id, double price1, double price2);
    List<Item> findAllByNameLikeAndBrand_IdAndPriceBetweenOrderByPriceAsc(String name, Long brand_id, double price1, double price2);
    List<Item> findAllByNameLikeAndBrand_IdAndPriceBetweenOrderByPriceDesc(String name, Long brand_id, double price1, double price2);
    List<Item> findAllByBrandIdAndPriceBetweenOrderByPriceAsc(Long brand_id, double price1, double price2);
    List<Item> findAllByBrandIdAndPriceBetweenOrderByPriceDesc(Long brand_id, double price1, double price2);
    List<Item> findAllByBrandIdAndNameLikeAndPriceBetweenOrderByPriceAsc(Long brand_id, String name, double price1, double price2);
    List<Item> findAllByBrandIdAndNameLikeAndPriceBetweenOrderByPriceDesc(Long brand_id, String name, double price1, double price2);

}
