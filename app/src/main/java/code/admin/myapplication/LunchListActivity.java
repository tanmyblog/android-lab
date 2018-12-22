package code.admin.myapplication;

import android.app.TabActivity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class LunchListActivity extends TabActivity {

    EditText name, addr;
    Button save;
    ListView listView;
    private List<Restaurant> restaurantList = new ArrayList<Restaurant>();
    RestaurantAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch_list);

        save = (Button)findViewById(R.id.btnSave);
        save.setOnClickListener(onSave);
        name = (EditText)findViewById(R.id.name);
        addr = (EditText)findViewById(R.id.addr);

        listView = (ListView)findViewById(R.id.lstRes);

        adapter = new RestaurantAdapter();

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(onListClick);

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
    public void setTabIcon(TabHost tabHost, int tabIndex, int iconResource) {
        ImageView tabImageView = (ImageView) tabHost.getTabWidget().getChildTabViewAt(tabIndex).findViewById(android.R.id.icon);
        tabImageView.setVisibility(View.VISIBLE);
        tabImageView.setImageResource(iconResource);
    }
    // class Restaurant inner class LunchActivity
    class RestaurantAdapter extends ArrayAdapter<Restaurant>{
        public RestaurantAdapter (Context context, int textViewResourceId) {
            super(context, textViewResourceId);

        }

        public RestaurantAdapter () {
            super(LunchListActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, restaurantList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if(row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.row, null);
            }

            Restaurant r = restaurantList.get(position);

            ((TextView)row.findViewById(R.id.title)).setText(r.getName());
            ((TextView)row.findViewById(R.id.address)).setText(r.getAddress());
            ImageView icon = (ImageView)row.findViewById(R.id.icon);

            String type = r.getType();

            if(type.equals("Take Out"))
                icon.setImageResource(R.drawable.t_96);
            else if (type.equals("Sit Down"))
                icon.setImageResource(R.drawable.s_96);
            else
                icon.setImageResource(R.drawable.d_96);

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

            RadioGroup type = (RadioGroup)findViewById(R.id.type);
            switch (type.getCheckedRadioButtonId()){
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
            Toast.makeText(LunchListActivity.this,restaurant.getName()+" "+restaurant.getAddress()+" "+restaurant.getType(),Toast.LENGTH_LONG).show();
        }
    };

    private AdapterView.OnItemClickListener   onListClick = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Restaurant r = restaurantList.get(position); // lấy item được chọn
            EditText name;
            EditText address;
            RadioGroup types;

        // Tham chiếu đến các view trong details
            name = (EditText)findViewById(R.id.name);
            address = (EditText)findViewById(R.id.addr);
            types = (RadioGroup)findViewById(R.id.type);

        // thiết lập thông tin tương ứng
            name.setText(r.getName());
            address.setText(r.getAddress());
            if (r.getType().equals("Sit Down"))
                types.check(R.id.sit_down);
            else if (r.getType().equals("Take Out"))
                types.check(R.id.take_out);
            else
                types.check(R.id.delivery);
        // sinh viên có thể bổ sung lệnh sau để chuyển view về tab details
            getTabHost().setCurrentTab(1);
        }
    };
}
