package fpt.isc.nshreport.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.BuildConfig;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.activities.Fragment.DebtReportActivity;
import fpt.isc.nshreport.activities.Fragment.GasPumpManageActivity;
import fpt.isc.nshreport.activities.Fragment.HomeActivity;
import fpt.isc.nshreport.activities.Fragment.ImportActivity;
import fpt.isc.nshreport.activities.Fragment.PriceActivity;
import fpt.isc.nshreport.activities.Fragment.SalesReportActivity;
import fpt.isc.nshreport.activities.Fragment.TankActivity;
import fpt.isc.nshreport.adapters.NotificationAdapter;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.customView.CallBackConfirmDialog;
import fpt.isc.nshreport.models.objectParse.BaseResponse;
import fpt.isc.nshreport.models.objectParse.NotificationList;
import fpt.isc.nshreport.models.objectParse.ShiftOpen;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.EndlessScrollListener;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Declares variables
    private TextView txtName, txtEmail;
    private ImageView imgAvatar;
    private DrawerLayout drawer;

    private NotificationAdapter adapter;
    private List<NotificationList.data> mItemNotificationList = new ArrayList<>();
    private ListView lvNavRight;
    private SwipeRefreshLayout swipeContainer;

    private ProgressBar progressBar_main;
    private NavigationView navigationViewLeft;
    private MenuItem nav_shift;
    private ShiftOpen shiftOpen;
    private boolean isloaded_shift = false;
    private User user;

    private Menu menu;
    private EventBus bus = EventBus.getDefault();

    private final int CHANGE_SHIFT = 124;
    private final int CREATE_SHIFT = 125;
    private final int APPROVE_IMPORT = 126;
    private final int UPDATE_SHIFT = 127;
    private final int PAYMENT = 128;

    private boolean firstBackPressed = false;

    private  final class onLoadingMoreDataTask implements EndlessScrollListener.onActionListViewScroll {

        @Override
        public void onApiLoadMoreTask(int page) {

            getNotification(page);

        }
    }

    /**
     * Constructor
     */
    public MainActivity(){ super(R.layout.activity_main);}

    /**
     * Create a intent to call this activity from another activity
     * @param context
     * @return
     */
    public static Intent buildIntent(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //Set up support tool for App
        setupSupportForApp();
        //display Home activity when the activity is loaded
        //displaySelectedFragment(R.id.nav_home);
        navigationViewLeft.setCheckedItem(R.id.nav_home);
        displaySelectedFragment(R.id.nav_home);
        //check user is create shift or not
        user = NSHSharedPreferences.getInstance(this).getUserLogin();
    }

    /**
     * Set up support tool for App
     */
    private void setupSupportForApp(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            @Override
            public void onDrawerOpened(View drawerView) {

                if(drawerView.getId() == R.id.nav_view_right)
                {
                    if(adapter == null && mItemNotificationList.size()==0) {
                        getNotification(1);
                    }
                }
            }

        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //menu left
        navigationViewLeft = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationViewLeft.getMenu();
        nav_shift = menu.findItem(R.id.nav_shift);
        navigationViewLeft.setNavigationItemSelectedListener(this);


        //Set data for navigation header
        View view = navigationViewLeft.getHeaderView(0);
        txtName = (TextView) view.findViewById(R.id.txtNameNavigation);
        txtEmail = (TextView) view.findViewById(R.id.txtEmailNavigation);
        imgAvatar = (ImageView) view.findViewById(R.id.imgNavigation);
        User user = getNSHSharedPreferences().getUserLogin();
        txtName.setText(user.getName());
        txtEmail.setText(user.getTitle_station());
        if(user.getAvatar() != null || user.getAvatar().length() > 3)
        {
            Picasso.with(this).load("http://inside.nshpetro.com.vn/"+user.getAvatar()).fit().centerInside().into(imgAvatar);
        }

        setupNavigationViewRight();

        progressBar_main = (ProgressBar) findViewById(R.id.progressBar_main);
    }

    private void checkIsGetShift()
    {
        isloaded_shift = false;
        progressBar_main.setAlpha(1.0f);
        mCompositeDisposable.add(
                NSHServer.getServer().reportOpen(user.getApi_token())
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse,this::handleError));
    }

    private void handleError(Throwable throwable) {
        //Toast.makeText(this, throwable.toString(), Toast.LENGTH_SHORT).show();
        showMessage(throwable.getMessage(),"Lỗi không xác định", SweetAlertDialog.ERROR_TYPE);
        progressBar_main.setAlpha(0);
        this.shiftOpen = null;
    }

    private void handleResponse(ShiftOpen shiftOpen) {
        if(shiftOpen.status.equals("success"))
        {
            nav_shift.setTitle("Chi tiết ca");
            this.shiftOpen = shiftOpen;
        }else
        {
            nav_shift.setTitle("Mở ca");
            this.shiftOpen = null;
        }
        progressBar_main.setAlpha(0);
        isloaded_shift = true;
    }

    private void getNotification(int page)
    {
        lvNavRight.setEnabled(false);
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeContainer.setRefreshing(true);
            }
        });
        mCompositeDisposable.add(
                NSHServer.getServer().notifyList(user.getApi_token(),page)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(this::handleResponseNotify,this::handleErrorNotify));
    }

    private void handleErrorNotify(Throwable throwable) {
        //Toast.makeText(this, throwable.toString(), Toast.LENGTH_SHORT).show();
        showMessage(throwable.getMessage(),"Lỗi không xác định", SweetAlertDialog.ERROR_TYPE);
        swipeContainer.setRefreshing(false);
        lvNavRight.setEnabled(true);
    }

    private void handleResponseNotify(NotificationList rs) {

        mItemNotificationList.addAll(rs.data);
        if(adapter == null)
        {
            adapter = new NotificationAdapter(this,mItemNotificationList);
            lvNavRight.setAdapter(adapter);
        }else
        {
            adapter.notifyDataSetChanged();
        }
        lvNavRight.setOnScrollListener(new EndlessScrollListener(rs.current_page,rs.last_page,new onLoadingMoreDataTask()));
        swipeContainer.setRefreshing(false);
        lvNavRight.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode) {
                case CHANGE_SHIFT: {
                    //start stepp 2 - Open new shift
                    Intent intent = ShiftAddActivity.buildIntent(getApplicationContext());
                    intent.putExtra("option", "change");
                    startActivityForResult(intent, CREATE_SHIFT);
                    break;
                }
                case UPDATE_SHIFT:
                case CREATE_SHIFT:
                {
                    checkIsGetShift();
                    break;
                }
            }
        }
    }

    private void setupNavigationViewRight()
    {
        lvNavRight = (ListView) findViewById(R.id.lvNotify);

        ColorDrawable sage = new ColorDrawable(ContextCompat.getColor(this, R.color.colorTop));
        lvNavRight.setDivider(sage);
        lvNavRight.setDividerHeight(3);
        lvNavRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //close menu right
                drawer.closeDrawer(GravityCompat.END);

                NotificationList.data data = mItemNotificationList.get(position);
                //new code
                //if(data.object_id > 0) {
                if(data.view_name_app_android.length() > 1 && data.is_read==0){
                    try {
                        //test
                        //data.view_name_app_android = "fpt.isc.nshreport.activities.ShiftCloseActivity";
                        Class<?> clazz = Class.forName(data.view_name_app_android);
                        Intent intent = new Intent(MainActivity.this, clazz);
                        switch (data.view_name_app_android)
                        {
                            case "fpt.isc.nshreport.activities.ShiftCloseActivity"://dong ca
                            {
                                if(shiftOpen != null) {
                                    //intent = new Intent(MainActivity.this,ShiftCloseActivity.class);
                                    mItemNotificationList.get(position).is_read = 1;
                                    adapter.notifyDataSetChanged();
                                    intent.putExtra("option", "close");
                                    intent.putExtra("notifi_id", data.notification_id);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("shift_data", shiftOpen);
                                    intent.putExtras(bundle);

                                    startActivityForResult(intent, UPDATE_SHIFT);
                                }else
                                {
                                    showMessage("Không có ca đang mở!",SweetAlertDialog.WARNING_TYPE);
                                }
                                break;
                            }
                            case "fpt.isc.nshreport.activities.ShiftAddActivity"://mo ca
                            {
                                if (shiftOpen == null) {
                                    //intent = new Intent(MainActivity.this,ShiftAddActivity.class);
                                    mItemNotificationList.get(position).is_read = 1;
                                    adapter.notifyDataSetChanged();
                                    //create shift
                                    intent.putExtra("notifi_id",data.notification_id);
                                    intent.putExtra("option", "add");
                                    startActivityForResult(intent,CREATE_SHIFT);
                                } else {
                                    //Toast.makeText(MainActivity.this, "Có ca đang mở", Toast.LENGTH_SHORT).show();
                                    showMessage("Có ca đang mở!",SweetAlertDialog.WARNING_TYPE);
                                }
                                break;
                            }
                            case "fpt.isc.nshreport.activities.ImportDetailActivity":
                            {
                                mItemNotificationList.get(position).is_read = 1;
                                adapter.notifyDataSetChanged();

                                intent.putExtra("objectID",data.object_id);
                                intent.putExtra("isApproved",false);
                                intent.putExtra("notifi_id",data.notification_id);
                                startActivityForResult(intent,APPROVE_IMPORT);
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                try {
                    mItemNotificationList.clear();
                    getNotification(1);
                }catch(Exception ex){}

            }
        });
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        //new code
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(!firstBackPressed) {
                firstBackPressed = true;
                //super.onBackPressed();
                //moveTaskToBack(true);
                //showMessage("Nhấn lần nửa để thoát ứng dụng","Xác nhận",SweetAlertDialog.WARNING_TYPE);
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitleText("Xác nhận");
                sweetAlertDialog.setContentText("Nhấn lần nửa để thoát ứng dụng");
                sweetAlertDialog.setCanceledOnTouchOutside(true);
                sweetAlertDialog.setOnCancelListener(dialog -> {
                    firstBackPressed = false;
                });
                sweetAlertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if(keyCode == KeyEvent.KEYCODE_BACK)
                        {
                            dialog.dismiss();
                            moveTaskToBack(true);
                            MainActivity.super.onBackPressed();
                        }
                        return false;
                    }
                });
                sweetAlertDialog.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notify, menu);
        this.menu = menu;
        //set count first time
        setCount();
        return true;
    }

    private void setCount()
    {
        String count = NSHSharedPreferences.getNotifiCount(this);
        MenuItem itemCart = menu.findItem(R.id.action_notify);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        AppUtilities.setBadgeCount(this, icon, count);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case R.id.action_notify: {
                if(!drawer.isDrawerOpen(GravityCompat.END))
                {
                    drawer.openDrawer(GravityCompat.END);
                    ShortcutBadger.removeCount(getApplicationContext());
                    //new code
                    String countStr = NSHSharedPreferences.getNotifiCount(this);
                    if(!countStr.equals("0")) {
                        LayerDrawable icon = (LayerDrawable) item.getIcon();
                        AppUtilities.setBadgeCount(this, icon, "0");
                        NSHSharedPreferences.saveNotifiCount("0", this);
                        resetCountNotifiViaAPI();
                    }
                }
            }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //calling the method displaySelectedFragment and passing the id of selected menu
        //displaySelectedFragment(item.getItemId());

            if (!navigationViewLeft.getMenu().findItem(item.getItemId()).isChecked()) {

                displaySelectedFragment(item.getItemId());
            } else {
                drawer.closeDrawer(GravityCompat.START);
            }

        //make this method blank
        return true;
    }



    /**
     * Go to distance activity when menu item to be selected
     * @param itemId
     */
    private void displaySelectedFragment(int itemId){
        //Create fragment object
        Fragment fragment = null;
        //initializing the fragment object which is selected
        switch (itemId){
            case R.id.nav_home:
                //fragment = new HomeActivityOld();
                fragment = new HomeActivity();
                break;
            case R.id.nav_shift:
            {
                //startShiftCreate();
                if(isloaded_shift) {
                    if (this.shiftOpen == null) {
                        //create shift
                        startShiftCreate();
                    } else {
                        //detail of shift
                        startDetailShift();
                    }
                }
                break;
            }
            case R.id.nav_salesreport:
                fragment = new SalesReportActivity();
                break;
            case R.id.nav_gaspumpmanage:
                fragment = new GasPumpManageActivity();
                break;

            case R.id.nav_inventory:
                //fragment = new InventoryActivity();
                fragment = new TankActivity();
                break;
            case R.id.nav_app_info: {
                showAppInformation();
                break;
            }
            case R.id.nav_app_log_out:
                logoutEvents();
                //test code
                //Intent intent = ShiftAddActivity.buildIntent(MainActivity.this);
                //startActivityForResult(intent, CREATE_SHIFT);

                break;
            case R.id.nav_price:
            {
                fragment = new PriceActivity();
                break;
            }
            case R.id.nav_import:
            {
                fragment = new ImportActivity();
                break;
            }
            case R.id.nav_payment:
            {
                startPayment();
                break;
            }
            case R.id.nav_paymentHis:
            {
                fragment = new DebtReportActivity();
                break;
            }
        }
        //replacing the fragment
        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void showAppInformation()
    {
        String version = BuildConfig.VERSION_NAME;
        String releaseDate = AppUtilities.ConvertStringDateToStringDate(BuildConfig.RELEASE_DATE,"yyyy-MM-dd","dd/MM/yyyy");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_app_info, null);
        ((TextView)dialogView.findViewById(R.id.txt_version)).setText(version);
        ((TextView)dialogView.findViewById(R.id.txt_release_date)).setText(releaseDate);

        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();
    }

    private void startPayment()
    {
        //Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
        Intent intent = PaymentActivity.buildIntent(getApplicationContext());
        startActivityForResult(intent, PAYMENT);
    }

    private void startShiftCreate()
    {
        Intent intent = ShiftAddActivity.buildIntent(getApplicationContext());
        intent.putExtra("option", "add");
        startActivityForResult(intent, CREATE_SHIFT);
    }
    private void startDetailShift()
    {
        Intent intent = ShiftDetailActivity.buildIntent(getApplicationContext());
        Bundle bundle = new Bundle();
        bundle.putSerializable("shift_data", shiftOpen);
        intent.putExtras(bundle);
        startActivityForResult(intent, UPDATE_SHIFT);
    }


    /**
     * Log out Application
     */
    private void logoutEvents(){
        AppUtilities.showConfirm(MainActivity.this, "Xác nhận", "Đăng xuất tài khoản " + user.getName(), "Đồng ý", "Hủy", SweetAlertDialog.WARNING_TYPE,new CallBackConfirmDialog() {
            @Override
            public void DiaglogPositive() {
                getNSHSharedPreferences().clearData();
                Intent intent = new Intent(MainActivity.this, FirstActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void DiaglogNegative() {
                //navigationViewLeft.setCheckedItem(menuPre);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
        checkIsGetShift();
        //get count and draw to icon notification
        if(menu != null) {
            setCount();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String data)
    {
        if(menu != null)
        {
            setCount();
        }
    }
    public void resetCountNotifiViaAPI()
    {
        NSHServer.getServer().resetNotifyCount(NSHSharedPreferences.getToken(this))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError);
    }

    private void handleResponse(BaseResponse baseResponse) {
        //Toast.makeText(this, baseResponse.message, Toast.LENGTH_SHORT).show();
    }



}
