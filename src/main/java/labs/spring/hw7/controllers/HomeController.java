package labs.spring.hw7.controllers;

import labs.spring.hw7.entities.*;
import labs.spring.hw7.services.ItemService;
import labs.spring.hw7.services.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${file.avatar.viewPath}")
    private String viewPath;

    @Value("${file.avatar.uploadPath}")
    private String uploadPath;

    @Value("${file.avatar.defaultPicture}")
    private String defaultPicture;

    @Value("${file.picture.viewPath}")
    private String imagesViewPath;

    @Value("${file.picture.uploadPath}")
    private String imagesUploadPath;

    @GetMapping(value = "/")
    public String index(Model model, HttpSession session){

        model.addAttribute("currentUser", getUserData());

        List<Item> item_list = itemService.getAllItems();
        model.addAttribute("items", item_list);

        List<Countries> country_list = itemService.getAllCountries();
        model.addAttribute("countries", country_list);

        List<Brands> brand_list = itemService.getAllBrands();
        model.addAttribute("brands", brand_list);

        getBasketData(session, model);

        return "index";
    }

    @PostMapping(value = "/additem")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addItem(@RequestParam(name = "item_name") String name,
                          @RequestParam(name = "brand_id") Long id,
                          @RequestParam(name = "item_description") String description,
                          @RequestParam(name = "item_price") double price,
                          @RequestParam(name = "item_small_picture") String smallPicture,
                          @RequestParam(name = "item_stars") int stars,
                          @RequestParam(name = "item_large_picture") String largePicture,
                          @RequestParam(name = "item_date") String date,
                          @RequestParam(name = "item_in_top", defaultValue = "False") boolean in_top){

        Brands brand = itemService.getBrand(id);

        if(brand != null){

            Item item = new Item();
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setSmallPicURL(smallPicture);
            item.setStars(stars);
            item.setLargePicURL(largePicture);
            item.setAddedData(date);
            item.setInTopPage(in_top);
            item.setBrand(brand);

            itemService.addItem(item);

        }

        return "redirect:/";
    }

    @PostMapping(value = "/addbrand")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addBrand(@RequestParam(name = "brand_name") String name,
                           @RequestParam(name = "country_id") Long id){

        Countries country = itemService.getCountry(id);

        if(country != null){

            Brands brand = new Brands();
            brand.setName(name);
            brand.setCountry(country);

            itemService.addBrand(brand);

        }

        return "redirect:/";
    }

    @PostMapping(value = "/addrole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addRole(@RequestParam(name = "role_name") String name,
                          @RequestParam(name = "role_description") String description){

        userService.addRole(new Roles(null, name, description));


        return "redirect:/addrole";
    }

    @PostMapping(value = "/addcountry")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addCountry(@RequestParam(name = "country_name") String name,
                             @RequestParam(name = "country_code") String code){

        itemService.addCountry(new Countries(null, name, code));

        return "redirect:/";
    }

    @GetMapping(value = "/details/{id}")
    public String details(Model model, @PathVariable(name = "id") Long id, HttpSession session){

        model.addAttribute("currentUser", getUserData());

        getBasketData(session, model);

        Item item = itemService.getItem(id);
        model.addAttribute("item", item);

        List<Pictures> picturesList = itemService.getAllByItem(id);
        model.addAttribute("pictures", picturesList);

        List<Countries> country_list = itemService.getAllCountries();
        model.addAttribute("countries", country_list);

        List<Comment> commentList = itemService.getCommentsByItem(id);
        model.addAttribute("comments", commentList);

        List<Brands> brand_list = itemService.getAllBrands();
        model.addAttribute("brands", brand_list);

        return "details";
    }

    @GetMapping(value = "/productdetails/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String productDetails(Model model, @PathVariable(name = "id") Long id){

        model.addAttribute("currentUser", getUserData());

        Item item = itemService.getItem(id);
        model.addAttribute("admin_item", item);

        List<Brands> brand_list = itemService.getAllBrands();
        model.addAttribute("item_brands", brand_list);

        List<Pictures> picturesList = itemService.getAllByItem(id);
        model.addAttribute("admin_pictures", picturesList);

        List<Categories> user_cat = item.getCategories();
        model.addAttribute("user_cat", user_cat);

        List<Categories> categories = itemService.getAllCategories();

        categories.removeAll(item.getCategories());

        model.addAttribute("categoties", categories);

        return "productdetails";
    }

    @GetMapping(value = "/roledetails/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String roleDetails(Model model, @PathVariable(name = "id") Long id){

        model.addAttribute("currentUser", getUserData());

        Roles role = userService.getRole(id);
        model.addAttribute("role", role);

        return "roledetails";
    }


    @GetMapping(value = "/userdetails/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String userDetails(Model model, @PathVariable(name = "email") String email){

        model.addAttribute("currentUser", getUserData());

        Users user = userService.getUserByEmail(email);
        if(user!= null){

            model.addAttribute("user", user);

            List<Roles> user_roles = user.getRoles();
            model.addAttribute("user_roles", user_roles);

            List<Roles> role_list = userService.getAllRoles();

            role_list.removeAll(user.getRoles());

            model.addAttribute("roles", role_list);

        }

        return "userdetails";
    }

    @PostMapping(value = "/unassignrole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String unassignRole(@RequestParam(name = "user_email") String email,
                             @RequestParam(name = "role_id") Long id){

        Roles role = userService.getRole(id);

        if(role != null){

            Users user = userService.getUserByEmail(email);

            if(user != null){

                List<Roles> roles = user.getRoles();
                if(roles == null){
                    roles = new ArrayList<>();
                }

                roles.remove(role);

                userService.saveUser(user);

                return "redirect:/userdetails/"+email+"#roleDiv";
            }

        }

        return "redirect:/listusers";
    }

    @PostMapping(value = "/assignrole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String assignRole(@RequestParam(name = "user_email") String email,
                             @RequestParam(name = "role_id") Long id){

        Roles role = userService.getRole(id);

        if(role != null){

            Users user = userService.getUserByEmail(email);

            if(user != null){

                List<Roles> roles = user.getRoles();
                if(roles == null){
                    roles = new ArrayList<>();
                }

                roles.add(role);

                userService.saveUser(user);

                return "redirect:/userdetails/"+email+"#roleDiv";
            }

        }

        return "redirect:/listusers";
    }

    @PostMapping(value = "/assigncategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String assignCategory(@RequestParam(name = "item_id") Long itemId,
                                 @RequestParam(name = "category_id") Long categoryId){

        Categories cat = itemService.getCategory(categoryId);

        if(cat != null){

            Item item = itemService.getItem(itemId);

            if(item != null){

                List<Categories> categories = item.getCategories();
                if(categories==null){
                    categories = new ArrayList<>();
                }

                categories.add(cat);

                itemService.saveItem(item);

                return "redirect:/productdetails/"+itemId+"#catDiv";

            }

        }

        return "redirect:/";
    }

    @PostMapping(value = "/unassigncategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String unassignCategory(@RequestParam(name = "item_id") Long itemId,
                                   @RequestParam(name = "category_id") Long categoryId){

        Categories cat = itemService.getCategory(categoryId);

        if(cat != null){

            Item item = itemService.getItem(itemId);

            if(item != null){

                List<Categories> categories = item.getCategories();
                if(categories==null){
                    categories = new ArrayList<>();
                }

                categories.remove(cat);

                itemService.saveItem(item);

                return "redirect:/productdetails/"+itemId+"#catDiv";

            }

        }

        return "redirect:/";
    }

    @GetMapping(value = "/countrydetails/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String countryDetails(Model model, @PathVariable(name = "id") Long id){

        model.addAttribute("currentUser", getUserData());

        Countries country = itemService.getCountry(id);
        model.addAttribute("admin_country", country);

        return "countrydetails";
    }

    @GetMapping(value = "/categorydetails/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String categoryDetails(Model model, @PathVariable(name = "id") Long id){

        model.addAttribute("currentUser", getUserData());

        Categories category = itemService.getCategory(id);
        model.addAttribute("admin_category", category);

        return "categorydetails";
    }

    @GetMapping(value = "/branddetails/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String brandDetails(Model model, @PathVariable(name = "id") Long id){

        model.addAttribute("currentUser", getUserData());

        Brands brand = itemService.getBrand(id);
        model.addAttribute("admin_brand", brand);

        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("admin_brand_countries", countries);

        return "branddetails";
    }

    @PostMapping(value = "/edititem")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editItem(@RequestParam(name = "edit_name") String name,
                           @RequestParam(name = "brand_id") Long brand_id,
                           @RequestParam(name = "edit_description") String description,
                           @RequestParam(name = "edit_price") double price,
                           @RequestParam(name = "edit_small_picture") String smallPicture,
                           @RequestParam(name = "edit_stars") int stars,
                           @RequestParam(name = "edit_large_picture") String largePicture,
                           @RequestParam(name = "edit_date") String date,
                           @RequestParam(name = "edit_in_top", defaultValue = "False") boolean in_top,
                           @RequestParam(name = "edit_id") Long id){

        Item item = itemService.getItem(id);

        if(item != null){

            Brands brand = itemService.getBrand(brand_id);

            if(brand != null){

                item.setName(name);
                item.setDescription(description);
                item.setPrice(price);
                item.setSmallPicURL(smallPicture);
                item.setLargePicURL(largePicture);
                item.setStars(stars);
                item.setAddedData(date);
                item.setInTopPage(in_top);
                item.setBrand(brand);

                itemService.saveItem(item);
            }

        }

        return "redirect:/productdetails/"+id;
    }

    @PostMapping(value = "/editrole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editRole(@RequestParam(name = "edit_role_id") Long id,
                           @RequestParam(name = "edit_role_name") String name,
                           @RequestParam(name = "edit_role_description") String description){

        Roles role = userService.getRole(id);

        if(role != null){

            role.setName(name);
            role.setDescription(description);

            userService.saveRole(role);

        }

        return "redirect:/roledetails/"+id;
    }

    @PostMapping(value = "/editbrand")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editBrand(@RequestParam(name = "edit_brand_id") Long id,
                            @RequestParam(name = "edit_brand_name") String name,
                            @RequestParam(name = "country_id") Long countryId){

        Brands brand = itemService.getBrand(id);

        if(brand != null){

            Countries country = itemService.getCountry(countryId);

            if(country != null){

                brand.setName(name);
                brand.setCountry(country);
                itemService.saveBrand(brand);

            }

        }

        return "redirect:/branddetails/"+id;
    }

    @PostMapping(value = "/editcategory")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editCategory(@RequestParam(name = "edit_category_id") Long id,
                               @RequestParam(name = "edit_category_name") String name,
                               @RequestParam(name = "edit_category_logo") String logo){

        Categories category = itemService.getCategory(id);

        if(category != null){

            category.setName(name);
            category.setLogoURL(logo);
            itemService.saveCategory(category);

        }

        return "redirect:/categorydetails/"+id;
    }

    @PostMapping(value = "/editcountry")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editCountry(@RequestParam(name = "edit_country_id") Long id,
                              @RequestParam(name = "edit_country_name") String name,
                              @RequestParam(name = "edit_country_code") String code){

        Countries country = itemService.getCountry(id);

        if(country != null){

            country.setName(name);
            country.setCode(code);
            itemService.saveCountry(country);

        }

        return "redirect:/countrydetails/"+id;
    }

    @PostMapping(value = "/edituser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editUser(@RequestParam(name = "edit_email") String email,
                           @RequestParam(name = "edit_user_fullname") String fullName){

        Users user = userService.getUserByEmail(email);

        if(user != null){

            user.setFullName(fullName);
            userService.saveUser(user);

        }

        return "redirect:/edituser/"+email;
    }


    @PostMapping(value = "/deleteitem")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String deleteItem(@RequestParam(name = "delete_id") Long id){

        Item item = itemService.getItem(id);

        if(item != null){
            itemService.deleteItem(item);
        }

        return "redirect:/";
    }

    @PostMapping(value = "/deletepicture")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String deletePicture(@RequestParam(name = "picture_id") Long id,
                                @RequestParam(name = "item_id") Long item_id){

        Pictures picture = itemService.getPicture(id);

        if(picture != null){
            itemService.deletePicture(picture);
            return "redirect:/productdetails/"+item_id;
        }

        return "redirect:/listproducts";
    }

    @GetMapping(value = "/search")
    public String search(Model model,
                         @RequestParam(name = "search_brand", defaultValue = "0") Long brand_id,
                         @RequestParam(name = "search_name", defaultValue = "") String name,
                         @RequestParam(name = "price_from", defaultValue = "0") double price1,
                         @RequestParam(name = "price_to", defaultValue = "100000000") double price2,
                         @RequestParam(name = "order", defaultValue = "Asc") String order,
                         HttpSession session){

        model.addAttribute("currentUser", getUserData());

        getBasketData(session, model);

        List<Brands> brand_list = itemService.getAllBrands();
        model.addAttribute("brands", brand_list);

        List<Countries> country_list = itemService.getAllCountries();
        model.addAttribute("countries", country_list);

        if(brand_id > 0){
            if(order.equals("Asc")) {
                List<Item> list = itemService.getItemsByBrandAndPriceAsc(brand_id, price1, price2);
                //List<Item> list = itemService.getItemsByNameAndBrandAndPriceAsc(name, brand_id, price1, price2);
                model.addAttribute("search_items", list);
            } else if(order.equals("Desc")){
                List<Item> list = itemService.getItemsByBrandAndPriceDesc(brand_id, price1, price2);
                //List<Item> list = itemService.getItemsByNameAndBrandAndPriceDesc(name, brand_id, price1, price2);
                model.addAttribute("search_items", list);
            }
        } else{
            if(order.equals("Asc")) {
                List<Item> list = itemService.getItemsByNameAndPriceAsc(name, price1, price2);
                model.addAttribute("search_items", list);
            } else if(order.equals("Desc")){
                List<Item> list = itemService.getItemsByNameAndPriceDesc(name, price1, price2);
                model.addAttribute("search_items", list);
            }
        }


        return "search";
    }

    @GetMapping(value = "/listproducts")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String listProducts(Model model){

        model.addAttribute("currentUser", getUserData());

        List<Item> item_list = itemService.getAllItems();
        model.addAttribute("admin_items", item_list);

        List<Brands> brand_list = itemService.getAllBrands();
        model.addAttribute("product_brands", brand_list);

        return "listproducts";
    }

    @GetMapping(value = "/listusers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String listUsers(Model model){

        model.addAttribute("currentUser", getUserData());

        List<Users> user_list = userService.getAllUsers();
        model.addAttribute("admin_users", user_list);

        return "listusers";
    }

    @GetMapping(value = "/listroles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String listRoles(Model model){

        model.addAttribute("currentUser", getUserData());

        List<Roles> role_list = userService.getAllRoles();
        model.addAttribute("admin_roles", role_list);

        return "listroles";
    }

    /*@GetMapping(value = "/listpictures")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String listPictures(Model model){
        model.addAttribute("currentUser", getUserData());

        List<Pictures> picturesList = itemService.gelAllPictures();
        model.addAttribute("admin_pictures", picturesList);

        return "listpictures";
    }*/

    @GetMapping(value = "/listcountries")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String listCountries(Model model){

        model.addAttribute("currentUser", getUserData());

        List<Countries> country_list = itemService.getAllCountries();
        model.addAttribute("admin_countries", country_list);

        return "listcountries";
    }

    @GetMapping(value = "/listbrands")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String listBrands(Model model){

        model.addAttribute("currentUser", getUserData());

        List<Brands> brand_list = itemService.getAllBrands();
        model.addAttribute("admin_brands", brand_list);

        List<Countries> country_list = itemService.getAllCountries();
        model.addAttribute("brand_countries", country_list);

        return "listbrands";
    }

    @GetMapping(value = "/listcategories")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String listCategories(Model model){

        model.addAttribute("currentUser", getUserData());

        List<Categories> category_list = itemService.getAllCategories();
        model.addAttribute("admin_categories", category_list);

        return "listcategories";
    }

    @GetMapping(value = "/listorders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String listOrders(Model model){

        model.addAttribute("currentUser", getUserData());

        List<Orders> ordersList = itemService.getAllOrders();
        model.addAttribute("admin_orders", ordersList);

        return "listorders";
    }

    @GetMapping(value = "/403")
    public String accessDenied(Model model, HttpSession session){
        model.addAttribute("currentUser", getUserData());
        getBasketData(session, model);
        return "403";
    }

    @GetMapping(value = "/login")
    public String login(Model model, HttpSession session){
        model.addAttribute("currentUser", getUserData());
        getBasketData(session, model);
        return "login";
    }

    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model, HttpSession session){
        model.addAttribute("currentUser", getUserData());
        List<Brands> brand_list = itemService.getAllBrands();
        model.addAttribute("brands", brand_list);
        getBasketData(session, model);
        return "profile";
    }

    private Users getUserData(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            User secUser = (User)authentication.getPrincipal();
            Users myUser = userService.getUserByEmail(secUser.getUsername());
            return myUser;
        }
        return null;
    }

    private void getBasketData(HttpSession session, Model model){
        Long order_id = (Long)session.getAttribute("basket");

        if(order_id != null){
            Orders order = itemService.getOrder(order_id);

            if(order.getItems().isEmpty()){
                String text = "empty";
                model.addAttribute("cart", text);
            } else{
                model.addAttribute("cart", order);
            }
        } else {
            String text = "empty";
            model.addAttribute("cart", text);
        }
    }

    @GetMapping(value = "/register")
    public String register(Model model, HttpSession session){

        model.addAttribute("currentUser", getUserData());
        getBasketData(session, model);
        return "register";
    }

    @PostMapping(value = "/register")
    public String toRegister(Model model,
                             @RequestParam(name = "user_email") String email,
                             @RequestParam(name = "user_password") String password,
                             @RequestParam(name = "re_user_password") String rePassword,
                             @RequestParam(name = "user_full_name") String fullName){

        if(password.equals(rePassword)){

            Users newUser = new Users();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setFullName(fullName);

            if(userService.createUser(newUser)!=null){
                return "redirect:/register?success";
            }

        }

        return "redirect:/register?error";

    }

    @PostMapping(value = "/adduser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addUser(Model model,
                          @RequestParam(name = "user_email") String email,
                          @RequestParam(name = "user_password") String password,
                          @RequestParam(name = "re_user_password") String rePassword,
                          @RequestParam(name = "user_full_name") String fullName){

        if(password.equals(rePassword)){

            Users newUser = new Users();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setFullName(fullName);

            if(userService.createUser(newUser)!=null){
                return "redirect:/listusers";
            }

        }

        return "redirect:/listroles";
    }

    @PostMapping(value = "/editfullname")
    @PreAuthorize("isAuthenticated()")
    public String editFullName(@RequestParam(name = "edit_email") String email,
                               @RequestParam(name = "edit_full_name") String fullName){

        Users user = userService.getUserByEmail(email);

        if(user != null){

            user.setFullName(fullName);
            userService.saveUser(user);

            return "redirect:/profile?success";
        }

        return "redirect:/profile?error";
    }

    @PostMapping(value = "/editpassword")
    @PreAuthorize("isAuthenticated()")
    public String editPassword(@RequestParam(name = "edit_pass_email") String email,
                               @RequestParam(name = "edit_password") String oldPassword,
                               @RequestParam(name = "new_user_password") String newPassword,
                               @RequestParam(name = "re_new_user_password") String reNewPassword){

        Users user = userService.getUserByEmail(email);

        if(user != null){

            if(passwordEncoder.matches(oldPassword, user.getPassword())){

                if(newPassword.equals(reNewPassword)){

                    user.setPassword(passwordEncoder.encode(newPassword));
                    userService.saveUser(user);
                    return "redirect:/profile?success";
                }

            }

        }

        return "redirect:/profile?error";
    }

    @PostMapping(value = "/uploadavatar")
    @PreAuthorize("isAuthenticated()")
    public String uploadAvatar(@RequestParam(name = "user_ava") MultipartFile file){

        if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {

            try {

                Users currentUser = getUserData();

                String picName = DigestUtils.sha1Hex("avatar_"+currentUser.getId()+"_!Picture");

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName + ".jpg");
                Files.write(path, bytes);

                currentUser.setUserAvatar(picName);
                userService.saveUser(currentUser);

                return "redirect:/profile?success";

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/profile?error";
    }

    @PostMapping(value = "/addpicture")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addPicture(@RequestParam(name = "item_pic") MultipartFile file,
                             @RequestParam(name = "p_item_id") Long id){

        Pictures picture = new Pictures();

        if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")){

            try{

                Item item = itemService.getItem(id);

                String picName = DigestUtils.sha1Hex("pictures_"+item.getId()+"_!newPicture"+file);

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName + ".jpg");
                Files.write(path, bytes);

                Date date = new Date();
                picture.setUrl(picName);
                picture.setItem(item);
                picture.setAddedDate(date.toString());
                itemService.savePicture(picture);

                return "redirect:/productdetails/"+id;

            } catch (Exception e){
                e.printStackTrace();
            }

        }

        return "redirect:/listproducts";
    }

    @GetMapping(value = "/viewphoto/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody byte[] viewProfilePhoto(@PathVariable(name = "url") String url) throws IOException {

        String pictureURL = viewPath+defaultPicture;

        if(url != null && !url.equals("null")){
            pictureURL = viewPath+url+".jpg";
        }

        InputStream in;

        try{

            ClassPathResource resource = new ClassPathResource(pictureURL);
            in = resource.getInputStream();

        }catch (Exception e){
            ClassPathResource resource = new ClassPathResource(viewPath+defaultPicture);
            in = resource.getInputStream();
            e.printStackTrace();
        }

        return IOUtils.toByteArray(in);
    }

    @GetMapping(value = "/viewpicture/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody byte[] viewPicturePhoto(@PathVariable(name = "url") String url) throws IOException {

        String pictureURL = viewPath+defaultPicture;

        if(url != null && !url.equals("null")){
            pictureURL = viewPath+url+".jpg";
        }

        InputStream in;

        try{

            ClassPathResource resource = new ClassPathResource(pictureURL);
            in = resource.getInputStream();

        }catch (Exception e){
            ClassPathResource resource = new ClassPathResource(viewPath+defaultPicture);
            in = resource.getInputStream();
            e.printStackTrace();
        }

        return IOUtils.toByteArray(in);
    }

    @GetMapping(value = "/basket")
    public String basket(Model model, HttpSession session){

        model.addAttribute("currentUser", getUserData());

        List<Brands> brand_list = itemService.getAllBrands();
        model.addAttribute("brands", brand_list);

        getBasketData(session, model);

        return "basket";
    }

    @PostMapping(value = "/addtobasket")
    public String CreateBasket(HttpSession session, @RequestParam(name = "item_id") Long id,
                               HttpServletRequest request, HttpServletResponse response){

        Long sesOrder = (Long)session.getAttribute("basket");

        Item item = itemService.getItem(id);

        Date date = new Date();

        if(sesOrder == null && item != null) {
            Orders order = new Orders();
            List<Item> items = new ArrayList<>();
            items.add(item);
            order.setItems(items);
            order.setStatus("New");
            order.setOrderDate(date.toString());

            itemService.addOrder(order);

            session.setAttribute("basket", order.getId());

            Cookie[] cookies = request.getCookies();

            for (Cookie c : cookies) {
                if (c.getName().equals("JSESSIONID")) {
                    c.setMaxAge(60 * 30);
                    response.addCookie(c);
                    break;
                }
            }
        } else{

            Orders order = itemService.getOrder(sesOrder);

            if (item != null){
                order.getItems().add(item);
                order.setOrderDate(date.toString());
                itemService.saveOrder(order);
            }

        }

        return "redirect:/basket";
    }

    @PostMapping(value = "/deletefrombasket")
    public String deleteFromBasket(HttpSession session, @RequestParam(name = "del_item_id") Long id){

        Long sesOrder = (Long)session.getAttribute("basket");

        Item item = itemService.getItem(id);

        Date date = new Date();

        if(sesOrder != null && item != null){

            Orders order = itemService.getOrder(sesOrder);

            if(order != null){

                order.getItems().remove(item);
                order.setOrderDate(date.toString());
                itemService.saveOrder(order);
            }

        }

        return "redirect:/basket";
    }

    @PostMapping(value = "/clearbasket")
    public String clearBasket(HttpSession session){

        Long sesOrder = (Long)session.getAttribute("basket");

        if(sesOrder != null){

            Orders order = itemService.getOrder(sesOrder);

            order.getItems().clear();

            itemService.saveOrder(order);

        }

        return "redirect:/basket";
    }

    @PostMapping(value = "/createorder")
    public String createOrder(HttpSession session, HttpServletResponse response, HttpServletRequest request){

        Long sesOrder = (Long)session.getAttribute("basket");

        Date date = new Date();

        if(sesOrder != null){

            Orders order = itemService.getOrder(sesOrder);

            order.setStatus("Created");
            order.setOrderDate(date.toString());

            itemService.saveOrder(order);

            Cookie[] cookies = request.getCookies();

            for (Cookie c : cookies) {
                if (c.getName().equals("JSESSIONID")) {
                    c.setMaxAge(0);
                    response.addCookie(c);
                    break;
                }
            }

        }

        return "redirect:/?success";
    }

    @GetMapping(value = "/savecomment/{id}")
    @PreAuthorize("isAuthenticated()")
    public String saveMyComment(Model model, @PathVariable(name = "id") Long id, HttpSession session){

        model.addAttribute("currentUser", getUserData());

        Comment comment = itemService.getComment(id);

        getBasketData(session, model);

        if(comment != null && comment.getAuthor() == getUserData()){

            List<Brands> brand_list = itemService.getAllBrands();
            model.addAttribute("brands", brand_list);

            model.addAttribute("comment", comment);

            return "savecomment";
        }

        return "redirect:/";
    }

    @PostMapping(value = "/savecomment")
    @PreAuthorize("isAuthenticated()")
    public String saveComment(@RequestParam(name = "edit_text") String text,
                             @RequestParam(name = "edit_item_id") Long itemId,
                              @RequestParam(name = "edit_comment_id") Long id){

        Date date = new Date();

        Comment comment = itemService.getComment(id);

        Item item = itemService.getItem(itemId);

        if(item != null){

            comment.setComment(text);
            comment.setAddedDate(date.toString());
            comment.setAuthor(getUserData());
            comment.setItem(item);

            itemService.saveComment(comment);

        }

        return "redirect:/details/"+itemId;
    }

    @PostMapping(value = "/addcomment")
    @PreAuthorize("isAuthenticated()")
    public String addComment(@RequestParam(name = "text") String text,
                             @RequestParam(name = "item_id") Long itemId){

        Date date = new Date();

        Item item = itemService.getItem(itemId);

        if(item != null){

            itemService.addNewCom(new Comment(null, text, date.toString(), item, getUserData()));

        }

        return "redirect:/details/"+itemId;
    }

    @PostMapping(value = "/deletecommnet")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@RequestParam(name = "comment_id") Long id,
                                @RequestParam(name = "item_id") Long itemId){

        Comment comment = itemService.getComment(id);

        if(comment != null){

            itemService.deleteComment(comment);

        }

        return "redirect:/details/"+itemId;
    }
}
