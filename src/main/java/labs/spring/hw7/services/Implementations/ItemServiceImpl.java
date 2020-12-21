package labs.spring.hw7.services.Implementations;

import labs.spring.hw7.entities.*;
import labs.spring.hw7.repositories.*;
import labs.spring.hw7.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Item getItem(Long id) {
        return itemRepository.getOne(id);
    }

    @Override
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public void deleteItem(Item item) {
        itemRepository.delete(item);
    }

    @Override
    public List<Item> getItemsByNameAsc(String name) {
        return itemRepository.findAllByNameLikeOrderByPriceAsc("%"+name+"%");
    }

    @Override
    public List<Item> getItemsByNameDesc(String name) {
        return itemRepository.findAllByNameLikeOrderByPriceDesc("%"+name+"%");
    }

    @Override
    public List<Item> getItemsByNameAndPriceAsc(String name, double price1, double price2) {
        return itemRepository.findAllByNameLikeAndPriceBetweenOrderByPriceAsc("%"+name+"%", price1, price2);
    }

    @Override
    public List<Item> getItemsByNameAndPriceDesc(String name, double price1, double price2) {
        return itemRepository.findAllByNameLikeAndPriceBetweenOrderByPriceDesc("%"+name+"%", price1, price2);
    }

    @Override
    public List<Item> getItemsByNameAndBrandAndPriceAsc(String name, Long brand_id, double price1, double price2) {
        return itemRepository.findAllByBrandIdAndNameLikeAndPriceBetweenOrderByPriceAsc(brand_id, name, price1, price2);
    }

    @Override
    public List<Item> getItemsByNameAndBrandAndPriceDesc(String name, Long brand_id, double price1, double price2) {
        return itemRepository.findAllByBrandIdAndNameLikeAndPriceBetweenOrderByPriceDesc(brand_id, name, price1, price2);
    }

    @Override
    public List<Item> getItemsByBrandAndPriceAsc(Long brand_id, double price1, double price2) {
        return itemRepository.findAllByBrandIdAndPriceBetweenOrderByPriceAsc(brand_id, price1, price2);
    }

    @Override
    public List<Item> getItemsByBrandAndPriceDesc(Long brand_id, double price1, double price2) {
        return itemRepository.findAllByBrandIdAndPriceBetweenOrderByPriceDesc(brand_id, price1, price2);
    }

    @Override
    public List<Countries> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Countries addCountry(Countries country) {
        return countryRepository.save(country);
    }

    @Override
    public Countries saveCountry(Countries country) {
        return countryRepository.save(country);
    }

    @Override
    public Countries getCountry(Long id) {
        return countryRepository.getOne(id);
    }

    @Override
    public List<Brands> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public Brands addBrand(Brands brand) {
        return brandRepository.save(brand);
    }

    @Override
    public Brands saveBrand(Brands brand) {
        return brandRepository.save(brand);
    }

    @Override
    public Brands getBrand(Long id) {
        return brandRepository.getOne(id);
    }

    @Override
    public List<Categories> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Categories getCategory(Long id) {
        return categoryRepository.getOne(id);
    }

    @Override
    public Categories addCategory(Categories category) {
        return categoryRepository.save(category);
    }

    @Override
    public Categories saveCategory(Categories category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Pictures> gelAllPictures() {
        return pictureRepository.findAll();
    }

    @Override
    public List<Pictures> getAllByItem(Long id) {
        return pictureRepository.findAllByItem_Id(id);
    }

    @Override
    public Pictures getPicture(Long id) {
        return pictureRepository.getOne(id);
    }

    @Override
    public Pictures addPicture(Pictures picture) {
        return pictureRepository.save(picture);
    }

    @Override
    public Pictures savePicture(Pictures picture) {
        return pictureRepository.save(picture);
    }

    @Override
    public void deletePicture(Pictures picture) {
        pictureRepository.delete(picture);
    }

    @Override
    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Orders addOrder(Orders order) {
        return orderRepository.save(order);
    }

    @Override
    public Orders getOrder(Long id) {
        return orderRepository.getOne(id);
    }

    @Override
    public void deleteOrder(Orders order) {
        orderRepository.delete(order);
    }

    @Override
    public Orders saveOrder(Orders order) {
        return orderRepository.save(order);
    }

    @Override
    public List<Comment> getCommentsByItem(Long id) {
        return commentRepository.findAllByItemId(id);
    }

    @Override
    public Comment addNewCom(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public Comment getComment(Long id) {
        return commentRepository.getOne(id);
    }
}
