package labs.spring.hw7.db;

import java.util.ArrayList;

public class DBManager {

    private static ArrayList<ShopItem> list = new ArrayList<>();

    static {
        list.add(new ShopItem(1L, "Xiaomi Redmi Note 9 pro", "Considering that the junior and senior versions have equal amounts of RAM," +
                " decide for yourself how important it is for you to have a spacious internal storage. Just in case," +
                " we note right away that the phone supports microSD cards up to 512 GB, " +
                "and has a separate slot for SIM cards and memory.", 126800, 50, 4, "https://xiaomi.kz/upload/medialibrary/cb3/cb33a22ff94756b94d5fe189e9e783f7.jpg"));
        list.add(new ShopItem(2L, "Ноутбук LENOVO Legion Y545", "(81Q60047RK) 15.6 FHD/Core i5 9300HF " +
                "2.4 Ghz/8/1TB+SSD128/GTX1660Ti/6/Dos", 460000, 20, 4, "https://www.mechta.kz/export/1cbitrix/import_files/01/0114e0c9-e38e-11ea-a230-005056b6dbd7.jpeg"));
        list.add(new ShopItem(3L, "Sony KD-65AG8", "4K HDR OLED TV with 4K HDR X1 ™" +
                " Extreme Processor and Acoustic Surface Audio technology.", 1119990, 10, 5, "https://sonycenter.kz/image/cachewebp_v/catalog/kostas/photo/tivi/new/ag8-600x600.webp"));
    }

    private static Long id = 4L;

    public static ArrayList<ShopItem> getShopItems(){
        return list;
    }

    public static void addShopItem(ShopItem shopItem){
        shopItem.setId(id);
        list.add(shopItem);
        id++;
    }

    public static ShopItem getShopItem(Long id){
        for (ShopItem shopItem:list) {
            if(shopItem.getId().equals(id)) return shopItem;
        }
        return null;
    }

    public static void deleteShopItem(Long id){
        list.removeIf(shopItem -> shopItem.getId().equals(id));
    }

}
