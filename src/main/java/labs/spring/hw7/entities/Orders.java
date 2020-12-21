package labs.spring.hw7.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_date")
    private String orderDate;

    @Column(name = "status")
    private String status;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Item> items;

    public double getTotalPrice(){

        double sum = 0;
        for (Item item: items) {
            sum+=item.getPrice();
        }

        return sum;
    }

    public int getCount(Item checkItem){

        int count = 0;
        for (Item item:items) {
            if(item.getId().equals(checkItem.getId())){
                count++;
            }
        }

        return count;
    }

    public double getItemPrice(Item checkItem){

        double count = getCount(checkItem);

        return count * checkItem.getPrice();
    }

    public List<Item> getBasketItems(){

        boolean check = false;

        List<Item> itemList = new ArrayList<>();

        for(Item item: items){



            check = false;

            if(itemList.isEmpty()){
                itemList.add(item);
            } else{

                for(Item newItem: itemList){
                    if (newItem.getId().equals(item.getId())) {
                        check = true;
                        break;
                    }
                }

                if(!check){
                    itemList.add(item);
                }

            }

        }

        return itemList;
    }
}
