package com.example.nhatnguyen.caller;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;

public class PlaceCallActivity extends BaseActivity {


    private Button stopButton;
    private ListView mlistView;
    private Firebase firebase;
    private Button btnNumber1,btnNumber2,btnNumber3,btnNumber4,btnNumber5,btnNumber6,btnNumber7
            ,btnNumber8,btnNumber9,btnNumber0,btnStar,btnSharp,btnCall,btnBackspace;
    private EditText edtNumber;
    private ArrayList<User> users = new ArrayList<User>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maindisplay);
        //Set up firebase
        loadTabs();
        loadKeyboard();
        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://nhatnguyen-caller.firebaseio.com/");
        //Initial View
        edtNumber = (EditText)findViewById(R.id.edtNumber);
        edtNumber.setText("");
        mlistView = (ListView)findViewById(R.id.listView);

        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(buttonClickListener);
        //Load Users to listView
        loadListView();
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doCall(position);

            }
        });
    }

    private void loadKeyboard() {
        btnCall= (Button)findViewById(R.id.btnCall);
        btnSharp = (Button)findViewById(R.id.btnSharp);
        btnStar = (Button)findViewById(R.id.btnStar);
        btnNumber0 = (Button)findViewById(R.id.btnNumber0);
        btnNumber1 = (Button)findViewById(R.id.btnNumber1);
        btnNumber2 = (Button)findViewById(R.id.btnNumber2);
        btnNumber3 = (Button)findViewById(R.id.btnNumber3);
        btnNumber4 = (Button)findViewById(R.id.btnNumber4);
        btnNumber5 = (Button)findViewById(R.id.btnNumber5);
        btnNumber6 = (Button)findViewById(R.id.btnNumber6);
        btnNumber7 = (Button)findViewById(R.id.btnNumber7);
        btnNumber8 = (Button)findViewById(R.id.btnNumber8);
        btnNumber9 = (Button)findViewById(R.id.btnNumber9);
        btnBackspace = (Button)findViewById(R.id.btnBackSpace);
        btnNumber0.setOnClickListener(myClick);
        btnNumber1.setOnClickListener(myClick);
        btnNumber2.setOnClickListener(myClick);
        btnNumber3.setOnClickListener(myClick);
        btnNumber4.setOnClickListener(myClick);
        btnNumber5.setOnClickListener(myClick);
        btnNumber6.setOnClickListener(myClick);
        btnNumber7.setOnClickListener(myClick);
        btnNumber8.setOnClickListener(myClick);
        btnNumber9.setOnClickListener(myClick);
        btnCall.setOnClickListener(myClick);
        btnSharp.setOnClickListener(myClick);
        btnStar.setOnClickListener(myClick);
        btnBackspace.setOnClickListener(myClick);
    }

    public void loadTabs()
    {
        //Lấy Tabhost id ra trước (cái này của built - in android
        final TabHost tab=(TabHost) findViewById
                (android.R.id.tabhost);
        //gọi lệnh setup
        tab.setup();
        TabHost.TabSpec spec;
        //Tạo tab1
        spec=tab.newTabSpec("t1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Dial Pad");
        tab.addTab(spec);
        //Tạo tab2
        spec=tab.newTabSpec("t2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Contacts");

        tab.addTab(spec);
        //Thiết lập tab mặc định được chọn ban đầu là tab 0
        tab.setCurrentTab(1);
        //Ở đây Tôi để sự kiện này để các bạn tùy xử lý
        //Ví dụ tab1 chưa nhập thông tin xong mà lại qua tab 2 thì báo...
    }
    private String getNameByPhone(String phoneNumber){
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }
    private void loadListView() {

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User post = postSnapshot.getValue(User.class);
                    String name = getNameByPhone(post.getPhoneNumber());
                    if(name !=null) {
                        post.setName(name);
                        users.add(post);
                    }
                }
                ArrayAdapter adapter = new Adapter(PlaceCallActivity.this,users,R.layout.customlistview);
                mlistView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    protected void onServiceConnected() {
        TextView userName = (TextView) findViewById(R.id.loggedInName);
        userName.setText(getSinchServiceInterface().getUserName());

    }

    private void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }



    private void doCall(int position) {
        try {
            Call call = getSinchServiceInterface().callUser(users.get(position).getPhoneNumber());
            if (call == null) {
                // Service failed for some reason, show a Toast and abort
                Toast.makeText(this, "Service is not started. Try stopping the service and starting it again before "
                        + "placing a call.", Toast.LENGTH_LONG).show();
                return;
            }
            String callId = call.getCallId();
            Intent callScreen = new Intent(this, CallScreenActivity.class);
            callScreen.putExtra(SinchService.CALL_ID, callId);
            startActivity(callScreen);
        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }

    private OnClickListener buttonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {


                case R.id.stopButton:
                    stopButtonClicked();
                    break;

            }
        }
    };
    OnClickListener myClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
           switch (v.getId()){
               case R.id.btnNumber0:
                   edtNumber.setText(edtNumber.getText().toString()+"0");
                   break;
               case R.id.btnNumber1:
                   edtNumber.setText(edtNumber.getText().toString()+"1");
                   break;
               case R.id.btnNumber2:
                   edtNumber.setText(edtNumber.getText().toString()+"2");
                   break;
               case R.id.btnNumber3:
                   edtNumber.setText(edtNumber.getText().toString()+"3");
                   break;
               case R.id.btnNumber4:
                   edtNumber.setText(edtNumber.getText().toString()+"4");
                   break;
               case R.id.btnNumber5:
                   edtNumber.setText(edtNumber.getText().toString()+"5");
                   break;
               case R.id.btnNumber6:
                   edtNumber.setText(edtNumber.getText().toString()+"6");
                   break;
               case R.id.btnNumber7:
                   edtNumber.setText(edtNumber.getText().toString()+"7");
                   break;
               case R.id.btnNumber8:
                   edtNumber.setText(edtNumber.getText().toString()+"8");
                   break;
               case R.id.btnNumber9:
                   edtNumber.setText(edtNumber.getText().toString()+"9");
                   break;
               case R.id.btnStar:
                   edtNumber.setText(edtNumber.getText().toString()+"*");
                   break;
               case R.id.btnSharp:
                   edtNumber.setText(edtNumber.getText().toString()+"#");
                   break;
               case R.id.btnBackSpace:
                   if (!edtNumber.getText().toString().equals(""))
                    edtNumber.setText(edtNumber.getText().toString().substring(0,edtNumber.getText().toString().length()-1));
                   break;
               case R.id.btnCall:
                   if(!edtNumber.getText().toString().trim().equals("")) {
                       try {
                           Call call = getSinchServiceInterface().callUser(edtNumber.getText().toString().trim());
                           if (call == null) {
                               // Service failed for some reason, show a Toast and abort
                               Toast.makeText(PlaceCallActivity.this, "Service is not started. Try stopping the service and starting it again before "
                                       + "placing a call.", Toast.LENGTH_LONG).show();
                               return;
                           }
                           String callId = call.getCallId();
                           Intent callScreen = new Intent(PlaceCallActivity.this, CallScreenActivity.class);
                           callScreen.putExtra(SinchService.CALL_ID, callId);
                           startActivity(callScreen);
                       } catch (MissingPermissionException e) {
                           ActivityCompat.requestPermissions(PlaceCallActivity.this, new String[]{e.getRequiredPermission()}, 0);
                       }
                   }
                   else  Toast.makeText(PlaceCallActivity.this, "Please fill in Number before Calling", Toast.LENGTH_LONG).show();
                   break;
           }
        }
    };

}
