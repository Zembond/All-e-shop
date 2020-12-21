package labs.spring.hw7.services;

import labs.spring.hw7.entities.*;

import java.util.List;

public interface ItemService {

    Item addItem(Item item);
    List<Item> getAllItems();
    Item getItem(Long id);
    Item saveItem(Item item);
    void deleteItem(Item item);
    List<Item> getItemsByNameAsc(String name);
    List<Item> getItemsByNameDesc(String name);
    List<Item> getItemsByNameAndPriceAsc(String name, double price1, double price2);
    List<Item> getItemsByNameAndPriceDesc(String name, double price1, double price2);
    List<Item> getItemsByNameAndBrandAndPriceAsc(String name, Long brand_id, double price1, double price2);
    List<Item> getItemsByNameAndBrandAndPriceDesc(String name, Long brand_id, double price1, double price2);
    List<Item> getItemsByBrandAndPriceAsc(Long brand_id, double price1, double price2);
    List<Item> getItemsByBrandAndPriceDesc(Long brand_id, double price1, double price2);


    List<Countries> getAllCountries();
    Countries addCountry(Countries country);
    Countries saveCountry(Countries country);
    Countries getCountry(Long id);

    List<Brands> getAllBrands();
    Brands addBrand(Brands brand);
    Brands saveBrand(Brands brand);
    Brands getBrand(Long id);

    List<Categories> getAllCategories();
    Categories getCategory(Long id);
    Categories addCategory(Categories category);
    Categories saveCategory(Categories category);

    List<Pictures> gelAllPictures();
    List<Pictures> getAllByItem(Long id);
    Pictures getPicture(Long id);
    Pictures addPicture(Pictures picture);
    Pictures savePicture(Pictures picture);
    void deletePicture(Pictures picture);

    List<Orders> getAllOrders();
    Orders addOrder(Orders order);
    Orders getOrder(Long id);
    void deleteOrder(Orders order);
    Orders saveOrder(Orders order);

    List<Comment> getCommentsByItem(Long id);
    Comment addNewCom(Comment comment);
    Comment saveComment(Comment comment);
    void deleteComment(Comment comment);
    Comment getComment(Long id);
}
