package com.deepwaterooo.dwsdk.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.deepwaterooo.dwsdk.R;
import com.deepwaterooo.dwsdk.activities.authentication.DWLoginActivity;
import com.deepwaterooo.dwsdk.appconfig.Constants;
import com.deepwaterooo.dwsdk.appconfig.JSONConstants;
import com.deepwaterooo.dwsdk.appconfig.Numerics;
import com.deepwaterooo.dwsdk.beans.PlayerDO;
import com.deepwaterooo.dwsdk.networklayer.ApiClient;
import com.deepwaterooo.dwsdk.networklayer.NetworkUtil;
import com.deepwaterooo.dwsdk.utils.ApiCallListener;
import com.deepwaterooo.dwsdk.utils.LoginListener;
import com.deepwaterooo.dwsdk.utils.PlayerUtil;
import com.deepwaterooo.dwsdk.utils.SharedPrefUtil;
import com.deepwaterooo.dwsdk.utils.Util;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity used for manage players of your account
 */

public class DWManagePlayerActivity // 不要这个类了
    // extends BluetoothBaseActivity // 我可以不用管这个类,就设置为单用户呀
    implements OnClickListener, ApiCallListener {
    @Override
    public void onClick(View view) {

    }

    @Override
    public void onResponse(Object object) {

    }

    @Override
    public void onFailure(Object error) {

    }

//     private static LoginListener listener;
//     private SharedPrefUtil sharedPrefUtil;
//     private Button btnEditPlayer;
//     private Button btnAddPlayer;
//     private Button btnBack;
//     private PlayersAdapter mPlayersAdapter;
//     private GridView mPlayersGridView;
//     private LinearLayout llFilters;
//     private TextView tvTitle;
//     private TextView tvFilterAll;
//     private TextView tvFilterABCD;
//     private TextView tvFilterEFGH;
//     private TextView tvFilterIJKL;
//     private TextView tvFilterMNO;
//     private TextView tvFilterPQRS;
//     private TextView tvFilterTUV;
//     private TextView tvFilterWXYZ;
//     private TextView tvAccountLabel;
//     private RelativeLayout rlRootView;
//     private boolean isManagePlayer = true;
//     private List<PlayerDO> playersList;
//     private TextView tvSelFilter;
//     private boolean isAddplayerClicked = false;

//     public static void setListener(LoginListener loginListener) {
//         listener = loginListener;
//     }

//     @Override
//     protected void onCreate(Bundle savedInstanceState) {
//         super.onCreate(savedInstanceState);
//         setContentView(R.layout.activity_manage_player);
//         sharedPrefUtil = new SharedPrefUtil(this);
//         if(!isUserLoggedIn()){
//             return;
//         }
//         if(!getIntent().hasExtra(Constants.EXTRA_CALL_APP_UPDATES_API)){
//             NetworkUtil.callGetAppUpdate(this);

//         }
//         intUI();
//         mPlayersGridView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
//         mPlayersGridView.setSelector(android.R.color.transparent);
//         mPlayersAdapter = new PlayersAdapter(SPManagePlayerActivity.this, null);
//         mPlayersGridView.setAdapter(mPlayersAdapter);
//         try {
//             callGetChildAPI(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN), sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID));
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         mPlayersGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                 @Override
//                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                     if (isManagePlayer/*btnEditPlayer.getText().toString().equals(getString(R.string.DoneEditing))*/) {
//                         PlayerDO playerDO = mPlayersAdapter.getItem(position);
//                         if (!isAddplayerClicked) {
//                             isAddplayerClicked = true;
//                             Intent intent = new Intent(SPManagePlayerActivity.this, SPAddEditPlayerActivity.class);
//                             intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                             intent.putExtra(Constants.EXTRA_PLAYER, playerDO);
//                             startActivityForResult(intent, Numerics.ZERO);
//                             new Handler().postDelayed(new Runnable() {
//                                     @Override
//                                     public void run() {
//                                         isAddplayerClicked = false;
//                                     }
//                                 }, 1000);
//                         }

//                     } else if (!isManagePlayer) {
//                         PlayerUtil.setSelectedPlayer(SPManagePlayerActivity.this, mPlayersAdapter.getItem(position));
//                         if (listener != null) {
//                             listener.didSelectedChild(mPlayersAdapter.getItem(position));
//                         }
//                         if (BluetoothUtil.isPlaysetConnected()) {
//                             BluetoothUtil.getLettersFromBoard(Numerics.ZERO);
//                         } else {
//                             Intent intent = new Intent(SPManagePlayerActivity.this, SPPlaysetScanActivity.class);
//                             intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                             intent.putExtra(Constants.EXTRA_IS_FROM_SELECT_PLAYER, true);
//                             startActivityForResult(intent, Numerics.ZERO);
//                         }
//                     }
//                 }
//             });

//         mPlayersGridView.postDelayed(new Runnable() {
//                 @Override
//                 public void run() {
//                     RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPlayersGridView.getLayoutParams();
//                     params.width = (int) (Constants.getDeviceWidth() * 0.7);
//                     mPlayersGridView.setLayoutParams(params);

//                 }
//             }, 100);
//     }

//     /**
//      * initialising the views used in this activity
//      */
//     private void intUI() {

//         rlRootView = (RelativeLayout) findViewById(R.id.rlRootView);

//         rlRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                 @Override
//                 public void onGlobalLayout() {
//                     int heightDiff = rlRootView.getRootView().getHeight() - rlRootView.getHeight();
//                     if (heightDiff > Util.dpToPx(SPManagePlayerActivity.this, Numerics.HUNDRED * Numerics.TWO)) {
//                         // if more than 200 dp, it's probably a keyboard...
//                     } else {
//                         hideSystemUI();
//                     }
//                 }
//             });

//         mPlayersGridView = (GridView) findViewById(R.id.gridPlayers);
//         btnEditPlayer = (Button) findViewById(R.id.btnEditPlayer);
//         btnAddPlayer = (Button) findViewById(R.id.btnAddPlayer);
//         btnBack = (Button) findViewById(R.id.btnBack);
//         tvTitle = (TextView) findViewById(R.id.tvTitle);
//         tvFilterAll = (TextView) findViewById(R.id.tvFilterAll);
//         tvFilterABCD = (TextView) findViewById(R.id.tvFilterABCD);
//         tvFilterEFGH = (TextView) findViewById(R.id.tvFilterEFGH);
//         tvFilterIJKL = (TextView) findViewById(R.id.tvFilterIJKL);
//         tvFilterMNO = (TextView) findViewById(R.id.tvFilterMNO);
//         tvFilterPQRS = (TextView) findViewById(R.id.tvFilterPQRS);
//         tvFilterTUV = (TextView) findViewById(R.id.tvFilterTUV);
//         tvFilterWXYZ = (TextView) findViewById(R.id.tvFilterWXYZ);
//         tvAccountLabel = (TextView) findViewById(R.id.tvAccountLabel);
//         llFilters = (LinearLayout) findViewById(R.id.llFilters);

//         btnEditPlayer.setOnClickListener(this);
//         btnAddPlayer.setOnClickListener(this);
//         btnBack.setOnClickListener(this);
//         tvFilterAll.setOnClickListener(this);
//         tvFilterABCD.setOnClickListener(this);
//         tvFilterEFGH.setOnClickListener(this);
//         tvFilterIJKL.setOnClickListener(this);
//         tvFilterMNO.setOnClickListener(this);
//         tvFilterPQRS.setOnClickListener(this);
//         tvFilterTUV.setOnClickListener(this);
//         tvFilterWXYZ.setOnClickListener(this);

//         if (getIntent().hasExtra(Constants.EXTRA_SELECT_PLAYER)) {
//             isManagePlayer = false;
//             btnEditPlayer.setVisibility(View.INVISIBLE);
//             btnEditPlayer.setClickable(false);
//             btnAddPlayer.setVisibility(View.INVISIBLE);
//             btnAddPlayer.setClickable(false);

//             tvTitle.setText(getString(R.string.SelectAPlayer));
//         } else {
//             ParentInfoDO parentInfo = PlayerUtil.getParentInfo(this);
//             if (parentInfo != null) {
//                 String username = parentInfo.getUsername();
//                 if (username != null && username.length() > 35){
//                     tvAccountLabel.setLines(2);
//                     tvAccountLabel.setText(username.substring(0,30)+"\n"+username.substring(30,username.length()));
//                 }else if(username != null){
//                     tvAccountLabel.setMaxLines(1);
//                     tvAccountLabel.setText(username);
//                 }else {
//                     tvAccountLabel.setText("");
//                 }
//             }
//             tvTitle.setText(getString(R.string.Manage_Player));
//         }
//         btnAddPlayer.setVisibility(View.INVISIBLE);
//         btnAddPlayer.setClickable(false);
//         tvFilterAll.performClick();
//     }

//     /**
//      * Listener for activity UI fields click events
//      *
//      * @param v return clicked view
//      */
//     @Override
//     public void onClick(View v) {

//         if (v.getId() == R.id.btnAddPlayer || v.getId() == R.id.ivAvatar) {
//             ParentInfoDO parentInfoDO = PlayerUtil.getParentInfo(this);
//             if (parentInfoDO != null && parentInfoDO.getRole().equalsIgnoreCase(getString(R.string.PARENT))) {
//                 if (!isAddplayerClicked) {
//                     isAddplayerClicked = true;
//                     Intent intent = new Intent(this, SPAddEditPlayerActivity.class);
//                     startActivityForResult(intent, Numerics.ZERO);
//                     new Handler().postDelayed(new Runnable() {
//                             @Override
//                             public void run() {
//                                 isAddplayerClicked = false;
//                             }
//                         }, 1000);
//                 }
//             } else {
//                 if (!isAddplayerClicked) {
//                     isAddplayerClicked = true;
//                     callTeacherContinue(null);
//                     new Handler().postDelayed(new Runnable() {
//                             @Override
//                             public void run() {
//                                 isAddplayerClicked = false;
//                             }
//                         }, 1000);
//                 }
//             }

//         } else if (v.getId() == R.id.btnBack) {
//             Class<?> className = null;
//             if (!getIntent().hasExtra(Constants.EXTRA_IS_FROM_GAME) && !isManagePlayer) {
//                 if (getString(R.string.game_activity) != null) {
//                     try {
//                         className = Class.forName(getString(R.string.game_activity));
//                         Intent gameIntent = new Intent(SPManagePlayerActivity.this, className);
//                         gameIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//                         startActivity(gameIntent);
//                         finish();
//                     } catch (ClassNotFoundException e) {
//                     }
//                 }
//             } else {
//                 try {
//                     className = Class.forName(getString(R.string.game_activity));
// //                    Intent gameIntent = new Intent(SPManagePlayerActivity.this, className);
// //                    gameIntent.putExtra(Constants.EXTRA_FROM_MENU, true);
// //                    gameIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
// //                    startActivity(gameIntent);
// //                    finish();

//                     Intent intent = new Intent();
//                     intent.putExtra(Constants.EXTRA_FROM_MENU, true);
//                     setResult(Constants.RESULT_BACK_TO_GAME, intent);
//                     finish();

//                 } catch (ClassNotFoundException e) {
//                 }

//             }

//         } else if (v.getId() == R.id.btnEditPlayer) {
//             if (btnEditPlayer.getText().toString().equals(getString(R.string.DoneEditing))) {
//                 btnEditPlayer.setBackgroundResource(R.drawable.white_round_rect_bg);
//                 btnEditPlayer.setText(getString(R.string.Edit_Player));
//                 if (mPlayersAdapter.getCount() < sharedPrefUtil.getInteger(SharedPrefUtil.PREF_PLAYERS_LIMIT)) {
//                     btnAddPlayer.setVisibility(View.VISIBLE);
//                     btnAddPlayer.setClickable(true);
//                 }
//                 if (mPlayersAdapter.getCount() == Numerics.ZERO) {
//                     btnEditPlayer.setVisibility(View.INVISIBLE);
//                     btnEditPlayer.setClickable(false);
//                 } else {
//                     btnEditPlayer.setVisibility(View.VISIBLE);
//                     btnEditPlayer.setClickable(true);
//                 }
//                 btnBack.setVisibility(View.VISIBLE);
//                 btnBack.setClickable(true);
//                 tvTitle.setText(getString(R.string.Manage_Player));
//             } else {
//                 btnEditPlayer.setBackgroundResource(R.drawable.button_background);
//                 btnEditPlayer.setText(getString(R.string.DoneEditing));
//                 btnAddPlayer.setVisibility(View.INVISIBLE);
//                 btnAddPlayer.setClickable(false);
//                 btnBack.setVisibility(View.INVISIBLE);
//                 btnBack.setClickable(false);
//                 tvTitle.setText(getString(R.string.EditPlayerMsg));
//             }
//         } else if (v.getId() == R.id.tvFilterAll || v.getId() == R.id.tvFilterABCD
//                    || v.getId() == R.id.tvFilterEFGH || v.getId() == R.id.tvFilterIJKL
//                    || v.getId() == R.id.tvFilterMNO || v.getId() == R.id.tvFilterPQRS
//                    || v.getId() == R.id.tvFilterTUV || v.getId() == R.id.tvFilterWXYZ) {

//             if (tvSelFilter != null) {
//                 tvSelFilter.setBackground(getResources().getDrawable(R.drawable.filter_bg_white));
//             }

//             tvSelFilter = (TextView) v;
//             tvSelFilter.setBackground(getResources().getDrawable(R.drawable.filter_bg));
//             if (playersList != null) {
//                 new FilterTask(tvSelFilter.getText().toString()).execute();
//             }
//         }
//     }

//     /**
//      * get the login user players list from the server
//      *
//      * @param tokenId  login user token id to access the server data
//      * @param parentId login user id
//      * @throws Exception handling exception
//      */
//     private void callGetChildAPI(String tokenId, String parentId) throws Exception {
//         playersList = null;
//         if (NetworkUtil.checkInternetConnection(this)) {
//             showProgressDialog(getString(R.string.Please_Wait));
//             ApiClient.getApiInterface(this).getChildAPI(JSONConstants.AUTHORIZATION_BEARER + tokenId,getString(R.string.gameID), parentId).enqueue(new Callback<List<PlayerDO>>() {
//                     @Override
//                     public void onResponse(Call<List<PlayerDO>> call, Response<List<PlayerDO>> response) {
//                         dismissProgressDialog();
//                         try {
//                             if (response.body() != null) {
//                                 playersList = response.body();
//                                 //Logger.debug("GetChild Success body", new Gson().toJson(playersList));
//                                 Util.savePlayersListToFile(SPManagePlayerActivity.this, new Gson().toJson(playersList));
//                                 if (playersList.size() > 0) {
//                                     Collections.sort(playersList, new FishNameComparator());
//                                 }
//                                 updateUI(playersList);
//                             } else if (response.code() == Constants.RESPONSE_CODE_UNAUTHORIZED) {
//                                 NetworkUtil.callReAuthenticationAPI(SPManagePlayerActivity.this, SPManagePlayerActivity.this, true);
//                             }
//                             if (playersList != null && playersList.size() > Numerics.NINE) {
//                                 if (tvSelFilter != null) {
//                                     tvSelFilter.performClick();
//                                 }
//                                 llFilters.setVisibility(View.VISIBLE);
//                             } else {
//                                 tvFilterAll.performClick();
//                                 llFilters.setVisibility(View.GONE);
//                             }
//                         } catch (IOException e) {
//                             e.printStackTrace();
//                         } catch (JSONException e) {
//                             e.printStackTrace();
//                         } catch (Exception e) {
//                             e.printStackTrace();
//                         }

//                     }

//                     @Override
//                     public void onFailure(Call<List<PlayerDO>> call, Throwable t) {
//                         dismissProgressDialog();
//                         getStoredPlayersList();
//                         //Logger.info("GetChild", "onFailure: " + t.toString());
//                         t.printStackTrace();
//                         if (playersList != null && playersList.size() > (Numerics.TEN + Numerics.TWO)) {
//                             llFilters.setVisibility(View.VISIBLE);
//                         } else {
//                             llFilters.setVisibility(View.GONE);
//                         }
//                     }
//                 });
//         } else {
//             getStoredPlayersList();
//         }
//     }

//     private void getStoredPlayersList() {
//         // continue with last login info in offline
//         String jsonString = Util.getPlayersListFromFile(SPManagePlayerActivity.this);
//         if (!TextUtils.isEmpty(jsonString)) {
//             Type listType = new TypeToken<List<PlayerDO>>() {
//                 }.getType();
//             playersList = new Gson().fromJson(jsonString, listType);
//             updateUI(playersList);
//             if (playersList != null && playersList.size() > (Numerics.TEN + Numerics.TWO)) {
//                 llFilters.setVisibility(View.VISIBLE);
//             } else {
//                 llFilters.setVisibility(View.GONE);
//             }
//         } else {
//             if (!NetworkUtil.checkInternetConnection(this)) {
//                 Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet),
//                                getString(R.string.Ok),null);
//             }

//         }
//     }

//     /**
//      * This method used update the UI using players
//      *
//      * @param playerDOList list of players for login user
//      */
//     private void updateUI(List<PlayerDO> playerDOList) {
//         mPlayersAdapter.refreshData((ArrayList<PlayerDO>) playerDOList);
//         if (!isManagePlayer && playerDOList.size() == 0
//             /*&& getIntent().hasExtra(Constants.EXTRA_IS_FIRST_TIME)*/) {
//             btnEditPlayer.setVisibility(View.INVISIBLE);
//             btnEditPlayer.setClickable(false);
//             ParentInfoDO parentInfoDO = PlayerUtil.getParentInfo(this);
//             if (parentInfoDO != null && parentInfoDO.getRole().equalsIgnoreCase(getString(R.string.PARENT))) {
//                 callZeroPlayerContinue(null, false);
// //                Intent intent = new Intent(SPManagePlayerActivity.this, SPAddNewPlayerActivity.class);
// //                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
// //                intent.putExtra(Constants.EXTRA_IS_FROM_SELECTION_PLAYER, true);
// //                startActivityForResult(intent, Numerics.ZERO);
//             } else {
//                 callZeroPlayerContinue(null, true);
//             }
//         } else if (!isManagePlayer && playerDOList.size() == Numerics.ONE &&
//                    (!getIntent().hasExtra(Constants.EXTRA_IS_FROM_GAME)
//                     || getIntent().hasExtra(Constants.EXTRA_IS_FIRST_TIME)
//                     || !BluetoothUtil.isPlaysetConnected())) {
//             if (listener != null) {
//                 listener.didSelectedChild(mPlayersAdapter.getItem(Numerics.ZERO));
//             }

//             PlayerUtil.setSelectedPlayer(SPManagePlayerActivity.this, mPlayersAdapter.getItem(Numerics.ZERO));
//             if (!BluetoothUtil.isPlaysetConnected() && !getIntent().hasExtra(Constants.EXTRA_IS_FROM_SELECT_PLAYER)) {
//                 Intent intent = new Intent(SPManagePlayerActivity.this, SPPlaysetScanActivity.class);
//                 intent.putExtra(Constants.EXTRA_IS_FROM_SELECT_PLAYER, true);
//                 startActivityForResult(intent, Numerics.ZERO);
//             } else {
//                 if(!getIntent().hasExtra(Constants.EXTRA_IS_FROM_SELECT_PLAYER)){
//                     setResult(Constants.RESULT_BACK_TO_GAME);
//                     finish();
//                 }
//             }
//         } else if (isManagePlayer) {
//             if (mPlayersAdapter.getCount() == Numerics.ZERO) {
//                 ParentInfoDO parentInfoDO = PlayerUtil.getParentInfo(this);
//                 if (parentInfoDO != null && parentInfoDO.getRole().equalsIgnoreCase(JSONConstants.TEACHER)) {
//                     callZeroPlayerContinue(null, true);
//                 }
//                 btnAddPlayer.setVisibility(View.VISIBLE);
//                 btnAddPlayer.setClickable(true);
//             } else {
//                 if (playerDOList.size() >= sharedPrefUtil.getInteger(SharedPrefUtil.PREF_PLAYERS_LIMIT)
//                     || btnEditPlayer.getText().toString().equals(getString(R.string.DoneEditing))) {
//                     btnAddPlayer.setVisibility(View.INVISIBLE);
//                     btnAddPlayer.setClickable(false);
//                 } else {
//                     btnAddPlayer.setVisibility(View.VISIBLE);
//                     btnAddPlayer.setClickable(true);
//                 }
//             }
//         } else if (playerDOList.size() > Numerics.ZERO) {
//             Util.playSound(SPManagePlayerActivity.this, Constants.AUDIO_SELECT_PLAYER);
//         }
//     }

//     /**
//      * Call Back method  to get the Message form other Activity
//      *
//      * @param requestCode request code
//      * @param resultCode  result code
//      * @param data        intent data from form other Activity
//      */
//     @Override
//     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//         super.onActivityResult(requestCode, resultCode, data);

//         if (resultCode == RESULT_OK) {
//             try {
//                 callGetChildAPI(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN), sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID));
//             } catch (Exception e) {
//                 getStoredPlayersList();
//                 e.printStackTrace();
//             }
//         }
//     }

//     @Override
//     protected void didNavigatesToMainMenu() {

//     }

//     @Override
//     public void bleLettersFromPlayset(String letters, byte[] hexData) {
//         super.bleLettersFromPlayset(letters, hexData);
//         boolean isLettersFound = false;
//         for (int i = 0; i < hexData.length; i++) {
//             if (!((hexData[i] + "").equals("0"))) {
//                 isLettersFound = true;
//             }
//         }
//         if (isLettersFound) {
//             Intent intent = new Intent(this, SPPlaysetCalibrationActivity.class);
//             intent.putExtra(Constants.EXTRA_DEVICE_ADDRESS, BluetoothUtil.getDeviceAddress());
//             intent.putExtra(Constants.EXTRA_IS_FROM_SELECTION_PLAYER, true);
//             startActivityForResult(intent, Numerics.ZERO);
//         }else{
//             setResult(Constants.RESULT_BACK_TO_GAME);
//             finish();
//         }
//     }
//     @Override
//     public void lettersFromPlayset(String letters, byte[] hexData) {
//     }

//     @Override
//     public void batteryLevel(String level) {

//     }

//     @Override
//     public void lcdsStates(byte[] status) {

//     }

//     @Override
//     public void connectedPlayset() {

//     }

//     @Override
//     public void availableServices() {

//     }

//     @Override
//     public void disconnectedPlayset() {

//     }

//     @Override
//     public void firmwareUpdateStatus(int progress) {

//     }

//     @Override
//     public void playsetName(String name) {

//     }

//     @Override
//     public void playsetFirmwareRevision(String rivision) {

//     }

//     @Override
//     public void playsetModelNumber(String modelNumber) {

//     }

//     @Override
//     public void playsetHardwareRevision(String hardwareRivision) {

//     }

//     @Override
//     public void playsetManufacturerName(String manufacturerName) {

//     }

//     @Override
//     public void gamePaused(boolean isScreenLocked) {

//     }

//     /**
//      * device back navigation control
//      */
//     @Override
//     public void onBackPressed() {
//     }

//     @Override
//     public void onResponse(Object object) {
//         try {
//             callGetChildAPI(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN), sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID));
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     @Override
//     public void onFailure(Object error) {

//     }

//     public class FilterTask extends AsyncTask<Void, Void, List<PlayerDO>> {

//         private String filterName;

//         public FilterTask(String filterName) {
//             this.filterName = filterName;
//         }

//         @Override
//         protected void onPreExecute() {
//             super.onPreExecute();

//             showProgressDialog(null);
//         }

//         @Override
//         protected List<PlayerDO> doInBackground(Void... params) {

//             if (filterName.replace("•", ",").split(",").length == Numerics.ONE) {
//                 return playersList;
//             } else {

//                 String filters[] = filterName.replace("•", ",").split(",");

//                 List<PlayerDO> tempList = new ArrayList<>();

//                 for (int i = 0; i < playersList.size(); i++) {

//                     for (int j = 0; j < filters.length; j++) {

//                         if (playersList.get(i).getFirstName().toLowerCase().startsWith(filters[j].toLowerCase())) {
//                             tempList.add(playersList.get(i));
//                         }
//                     }
//                 }

//                 return tempList;
//             }
//         }

//         @Override
//         protected void onPostExecute(List<PlayerDO> playerDOs) {
//             super.onPostExecute(playerDOs);
//             dismissProgressDialog();
//             mPlayersAdapter.refreshData((ArrayList<PlayerDO>) playerDOs);
//         }
//     }

//     public class FishNameComparator implements Comparator<PlayerDO> {
//         public int compare(PlayerDO left, PlayerDO right) {
//             return left.getFirstName().compareTo(right.getFirstName());
//         }
//     }

//     /**
//      * to check whether user has logged-in or Logged-out
//      */
//     private boolean isUserLoggedIn() {
//         if (!sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_LOGIN_USER_STATUS)) {
//             Intent i = new Intent(SPManagePlayerActivity.this, SPLoginActivity.class);
//             startActivityForResult(i, Numerics.ZERO);
//             return false;
//         }
//         return true;
//     }
}