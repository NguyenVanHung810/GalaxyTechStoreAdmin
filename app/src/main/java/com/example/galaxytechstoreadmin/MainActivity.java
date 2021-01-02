package com.example.galaxytechstoreadmin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static Toolbar toolbar;
    private Window window;
    private AppBarLayout.LayoutParams layoutParams;
    public static DrawerLayout drawer;
    private NavigationView navigationView;
    public static TextView actionbar_name;
    private int scrollFlags;
    public static int currentFragment = -1;
    public static final int HomeFragment = 0;
    public static final int OrdersFragment = 1;
    public static final int ProductFragment = 2;
    public static final int CategoryFragment = 3;
    public static final int AccountFragment = 4;
    private FirebaseUser currentUser;
    private TextView fullname, email;
    public static boolean resetMainActivity = false;
    public static MenuItem menuItem;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(false);
        } else {
            if (DBqueries.email == null) {
                FirebaseFirestore.getInstance().collection("ADMINS").document(currentUser.getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DBqueries.fullname = task.getResult().getString("name");
                            DBqueries.email = task.getResult().getString("email");
                            DBqueries.phone = task.getResult().getString("phonenumber");

                            fullname.setText(DBqueries.fullname);
                            email.setText(DBqueries.email);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                fullname.setText(DBqueries.fullname);
                email.setText(DBqueries.email);
            }
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(true);
        }

        if (resetMainActivity) {
            resetMainActivity = false;
            actionbar_name.setVisibility(View.VISIBLE);
            navigationView.getMenu().getItem(0).setChecked(true);
            setFragment(new HomeFragment(), HomeFragment);
        }
        invalidateOptionsMenu();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        actionbar_name = (TextView) findViewById(R.id.actionbar_name);
        setSupportActionBar(toolbar);

        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        layoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        scrollFlags = layoutParams.getScrollFlags();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        setFragment(new HomeFragment(), HomeFragment);

        fullname = navigationView.getHeaderView(0).findViewById(R.id.main_name);
        email = navigationView.getHeaderView(0).findViewById(R.id.main_email);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setFragment(Fragment fragment, int FragmentNo) {
        if (FragmentNo != currentFragment) {
            currentFragment = FragmentNo;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            ft.replace(R.id.main_layout, fragment, null);
            ft.setReorderingAllowed(true);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void gotoFragment(String tt, Fragment fragment, int FragmentNo) {
        actionbar_name.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(tt);
        invalidateOptionsMenu();
        setFragment(fragment, FragmentNo);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawers();
        menuItem = item;
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    actionbar_name.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), HomeFragment);
                } else if (id == R.id.nav_orders) {
                    gotoFragment("Quản lý đơn hàng", new OrderFragment(), OrdersFragment);
                } else if (id == R.id.nav_product) {
                    gotoFragment("Quản lý sản phẩm", new ProductFragment(), ProductFragment);
                } else if (id == R.id.nav_category) {
                    gotoFragment("Quản lý danh mục", new CategoryFragment(), CategoryFragment);
                }else if (id == R.id.nav_account) {
                    gotoFragment("Thông tin tài khoản", new AccountFragment(), AccountFragment);
                } else if (id == R.id.sign_out) {
                    FirebaseAuth.getInstance().signOut();
                    DBqueries.clearData();
                    DBqueries.email = null;
                    Intent registerIntent = new Intent(MainActivity.this, Login_Register_ResetPassword_Activity.class);
                    startActivity(registerIntent);
                    finish();
                }
                drawer.removeDrawerListener(this);
            }
        });
        return true;
    }
}