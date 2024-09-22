package com.example.crm;

import static com.example.crm.AppData.employeeId;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ManagerDashbord extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    SharedPreferences sp;
    Button approvetprogrambtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_dashbord);



        sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        initializeAppData();
      //  Toast.makeText(this, "Welcome " + AppData.manager_id, Toast.LENGTH_SHORT).show();

        approvetprogrambtn= findViewById(R.id.approvetprogrambtn);

        approvetprogrambtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to another activity
                Intent intent = new Intent(ManagerDashbord.this, ManagerApproval.class);
                startActivity(intent);
            }
        });


        // Initialize DrawerLayout and ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.drawer_layout1);
        Toolbar toolbar = findViewById(R.id.manager_dashbord_toolbar);
        setSupportActionBar(toolbar);


        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.OpenDrawer, R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        // Handle the click on the ImageView to open the drawer
        ImageView drawerToggleImage = findViewById(R.id.dcr_dashbord_back);
        drawerToggleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        // Find the NavigationView
        NavigationView navigationView = findViewById(R.id.navview);

        // Get the header view from the NavigationView
        View headerView = navigationView.getHeaderView(0);  // Ensure this index is correct

        // Initialize the TextViews from the header layout
        TextView headerName = headerView.findViewById(R.id.hedername);
        TextView headerDivision = headerView.findViewById(R.id.hederdivision);
        TextView headerEmail = headerView.findViewById(R.id.hederemail);
        TextView headerPhone = headerView.findViewById(R.id.hederphone);

        // Set text to the TextViews (Ensure you have data to populate)
        if (headerName != null) {
            headerName.setText("Name : " +AppData.employeeName);  // Set actual data here
        }
        if (headerDivision != null) {
            headerDivision.setText("Division: "+AppData.division);
        }
        if (headerEmail != null) {
            headerEmail.setText("Email : "+AppData.email);
        }
        if (headerPhone != null) {
            headerPhone.setText("Phone :"+AppData.phoneNumber);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.dashbordbutton) {
                    // Navigate to the dashboard and show a toast
                    Toast.makeText(ManagerDashbord.this, "DashBord View", Toast.LENGTH_SHORT).show();
                    // Close the drawer after selection
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (id == R.id.Logoutbutton) {
                    logout();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    private void initializeAppData() {
        // Initialize AppData with values from SharedPreferences
        AppData.employeeName = sp.getString("employeeName", "Default Name");
        employeeId = sp.getString("employeeId","");
        AppData.division = sp.getString("division", "Default Division");
        AppData.email = sp.getString("email", "Default Email");
        AppData.phoneNumber = sp.getString("phoneNumber", "Default Phone");
        AppData.manager_id = sp.getString("manager_id","Default manager_id");
        AppData.manager_Name = sp.getString("manager_Name","Default manager_Name");
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Reset isLoggedIn flag but keep login details if rememberMe is checked
        editor.putBoolean("isLoggedIn", false);
        if (!sharedPreferences.getBoolean("rememberMe", false)) {
            editor.remove("businessId");
            editor.remove("email");
            editor.remove("password");
            editor.putBoolean("rememberMe", false);
        }

        editor.apply();

        Intent intent = new Intent(ManagerDashbord.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close SecondActivity so it is removed from the back stack

        Toast.makeText(ManagerDashbord.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
    }
}