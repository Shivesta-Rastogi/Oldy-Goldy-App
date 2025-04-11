package com.example.myfinalloginpage;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        recyclerView = findViewById(R.id.recyclerView_order);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderList.add(new Order("Delivery expected by Apr 01", "table fan", R.drawable.fan));
        orderList.add(new Order("Refund completed", "hercules cycle", R.drawable.cycle));
        orderList.add(new Order("Refund completed", "2rd year 2nd sem books", R.drawable.books));
        orderList.add(new Order("Replacement Completed", "steel non rusted trunk", R.drawable.trunk));

        orderAdapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(orderAdapter);
    }
}
