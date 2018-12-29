package code.admin.myapplication;

import android.app.TabActivity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import code.admin.myapplication.model.RestaurantHelper;

public class LunchListActivity extends TabActivity {

    EditText name, addr;
    Button save;
    ListView listView;

    // khai bao danh sach restaurant
    private List<Restaurant> restaurantList = new ArrayList<Restaurant>();

    // khai bao danh sach restaurant dung Cursor
    Cursor curRestaurant = null;
    RestaurantAdapter adapter = null;

    // khai bao bien thanh vien lien quan den truy cap du lieu
    RestaurantHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch_list);

        save = (Button) findViewById(R.id.btnSave);
        save.setOnClickListener(onSave);
        name = (EditText) findViewById(R.id.name);
        addr = (EditText) findViewById(R.id.addr);

        listView = (ListView) findViewById(R.id.lstRes);

        //adapter = new RestaurantAdapter();

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(onListClick);

        // khoi tao doi duong RestaurantHelper
        helper = new RestaurantHelper(this);

        // lay du lieu tu CSDL
        curRestaurant = helper.getAll();
        startManagingCursor(curRestaurant);
        adapter = new RestaurantAdapter(curRestaurant);
        listView.setAdapter(adapter);

        // xu ly tabhost
        TabHost.TabSpec spec = getTabHost().newTabSpec("tag1");
        spec.setContent(R.id.lstRes);
        spec.setIndicator("  List", getResources().getDrawable(R.drawable.list, getTheme()));
        getTabHost().addTab(spec);

        spec = getTabHost().newTabSpec("tag2");
        spec.setContent(R.id.tblDetails);
        spec.setIndicator("  Details", getResources().getDrawable(R.drawable.restedit, getTheme()));

        getTabHost().addTab(spec);
        getTabHost().setCurrentTab(0);

        setTabIcon(getTabHost(), 0, R.drawable.list); //for Tab 1
        setTabIcon(getTabHost(), 1, R.drawable.restedit); //for Tab 2
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        // Đóng cơ sở dữ liệu
        helper.close();
    }// end onDestroy


    public void setTabIcon(TabHost tabHost, int tabIndex, int iconResource) {
        ImageView tabImageView = (ImageView) tabHost.getTabWidget().getChildTabViewAt(tabIndex).findViewById(android.R.id.icon);
        tabImageView.setVisibility(View.VISIBLE);
        tabImageView.setImageResource(iconResource);
    }

    // class Restaurant inner class LunchActivity
    class RestaurantAdapter extends CursorAdapter {
        public RestaurantAdapter(Cursor c) {
            super(LunchListActivity.this, c);
        }

        public RestaurantAdapter(Context context, Cursor c) {
            super(context, c);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // TODO Auto-generated method stub
            View row = view;
            ((TextView) row.findViewById(R.id.title)).
                    setText(helper.getName(cursor));
            ((TextView) row.findViewById(R.id.address)).
                    setText(helper.getAddress(cursor));
            ImageView icon = (ImageView) row.findViewById(R.id.icon);

            String type = helper.getType(cursor);
            if(type.equals("Take Out"))
                icon.setImageResource(R.drawable.t_96);
            else if (type.equals("Sit Down"))
                icon.setImageResource(R.drawable.s_96);
            else
                icon.setImageResource(R.drawable.d_96);
        }// end bindView

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);
            return row;
        }
    }

    // ham xu ly su kien click
    private View.OnClickListener onSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Restaurant restaurant = new Restaurant();

            restaurant.setName(name.getText().toString());
            restaurant.setAddress(addr.getText().toString());

            RadioGroup type = (RadioGroup) findViewById(R.id.type);
            switch (type.getCheckedRadioButtonId()) {
                case R.id.take_out:
                    restaurant.setType("Take Out");
                    break;
                case R.id.sit_down:
                    restaurant.setType("Sit Down");
                    break;
                case R.id.delivery:
                    restaurant.setType("Delivery");
                    break;
            }
            restaurantList.add(restaurant);
            Toast.makeText(LunchListActivity.this, restaurant.getName() + " " + restaurant.getAddress() + " " + restaurant.getType(), Toast.LENGTH_LONG).show();

            // them vao CSDL
            helper.insert(restaurant.getName(), restaurant.getAddress(),
                    restaurant.getType());

            // refesh lai du lieu
            curRestaurant.requery();
        }
    };

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // di chuyển cursor đến vị trí hiện tại là position
            curRestaurant.moveToPosition(position);

            EditText name;
            EditText address;
            RadioGroup types;

            // Tham chiếu đến các view trong details
            name = (EditText) findViewById(R.id.name);
            address = (EditText) findViewById(R.id.addr);
            types = (RadioGroup) findViewById(R.id.type);

            // sử dụng đối tượng helper để lấy các thông tin nhà hàng
            name.setText(helper.getName(curRestaurant));
            address.setText(helper.getAddress(curRestaurant));
            if (helper.getType(curRestaurant).equals("Sit down"))
                types.check(R.id.sit_down);
            else if (helper.getType(curRestaurant).equals("Take out"))
                types.check(R.id.take_out);
            else
                types.check(R.id.delivery);

            // sinh viên có thể bổ sung lệnh sau để chuyển view về tab details
            getTabHost().setCurrentTab(1);
        }
    };
}
