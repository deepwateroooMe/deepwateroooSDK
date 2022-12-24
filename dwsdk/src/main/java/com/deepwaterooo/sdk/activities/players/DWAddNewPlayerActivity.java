package com.deepwaterooo.sdk.activities.players;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.activities.DWAllSetForGameActivity;
import com.deepwaterooo.sdk.activities.DWBaseActivity;
import com.deepwaterooo.sdk.adapters.PlayersAdapter;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.JSONConstants;
import com.deepwaterooo.sdk.appconfig.Logger;
import com.deepwaterooo.sdk.appconfig.Numerics;
import com.deepwaterooo.sdk.beans.LoginUserDO;
import com.deepwaterooo.sdk.beans.PlayerDO;
import com.deepwaterooo.sdk.networklayer.ApiClient;
import com.deepwaterooo.sdk.networklayer.NetworkUtil;
import com.deepwaterooo.sdk.utils.ApiCallListener;
import com.deepwaterooo.sdk.utils.PlayerUtil;
import com.deepwaterooo.sdk.utils.SharedPrefUtil;
import com.deepwaterooo.sdk.utils.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity used for add a New player to your account
 */
public class DWAddNewPlayerActivity extends DWBaseActivity implements View.OnClickListener, ApiCallListener {

    private SharedPrefUtil sharedPrefUtil;
    //    private Button btnEditPlayer;
    private Button btnAddPlayer;
    private Button btnContinue;
    //    private ImageView ivAddPlayer;
    private RelativeLayout rlBottomLayout;
    private PlayersAdapter mPlayersAdapter;
    private RelativeLayout rlRootView;
    private GridView mPlayersGridView;
    //    private LinearLayout llFirstPlayer;
    private LoginUserDO loginUserDO;
    private TextView tvAddPlayersMsg;
    private LinearLayout llFilters;
    private TextView tvFilterAll;
    private TextView tvFilterABCD;
    private TextView tvFilterEFGH;
    private TextView tvFilterIJKL;
    private TextView tvFilterMNO;
    private TextView tvFilterPQRS;
    private TextView tvFilterTUV;
    private TextView tvFilterWXYZ;
    private TextView tvSelFilter;
    private List<PlayerDO> playersList;
    private boolean isAddplayerClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_player);
        sharedPrefUtil = new SharedPrefUtil(this);
        if(!getIntent().hasExtra(Constants.EXTRA_CALL_APP_UPDATES_API)){
            NetworkUtil.callGetAppUpdate(this);

        }
        intUI(false);
        mPlayersGridView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        mPlayersGridView.setSelector(android.R.color.transparent);
        mPlayersAdapter = new PlayersAdapter(DWAddNewPlayerActivity.this, null);
        mPlayersGridView.setAdapter(mPlayersAdapter);
        if (getIntent().getExtras() != null && !getIntent().getExtras().isEmpty() && getIntent().getExtras().getBoolean(Constants.EXTRA_IS_FROM_LOGIN)) {
            loginUserDO = getIntent().getExtras().getParcelable(Constants.EXTRA_LOGIN_USER);
            if (loginUserDO != null && loginUserDO.getPlayer() != null) {
                if (loginUserDO.getPlayer().size() == 0) {
                    intUI(true);
                    btnAddPlayer.setVisibility(View.VISIBLE);
                    btnAddPlayer.setClickable(true);
                } else if (loginUserDO.getPlayer().size() == 1) {
// 这里需要怎么跳过一下：不用扫描学习机
//                    Intent intent = new Intent(this, DWPlaysetScanActivity.class);
//                    startActivityForResult(intent, Numerics.ZERO);
                } else {
                    intUI(false);
                    mPlayersAdapter.refreshData(loginUserDO.getPlayer());
                }
            }
        } else if (getIntent().getExtras() != null && !getIntent().getExtras().isEmpty() &&
                   getIntent().getExtras().getBoolean(Constants.EXTRA_IS_FROM_SELECTION_PLAYER)) {
            intUI(true);
            btnAddPlayer.setVisibility(View.VISIBLE);
            btnAddPlayer.setClickable(true);
        } else {
            try {
                if (sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_LOGIN_USER_STATUS)) {
                    callGetChildAPI(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN), sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mPlayersGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    PlayerDO playerDO = mPlayersAdapter.getItem(position);
                    if (!isAddplayerClicked) {
                        isAddplayerClicked = true;
                        Intent intent = new Intent(DWAddNewPlayerActivity.this, DWAddEditPlayerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra(Constants.EXTRA_PLAYER, playerDO);
                        startActivityForResult(intent, Numerics.ZERO);
                        new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isAddplayerClicked = false;
                                }
                            }, 1000);

                    }
                }
            });
    }


    /**
     * initialising the views used in this activity
     */
    private void intUI(boolean isAddNewPlayer) {

        rlRootView = (RelativeLayout) findViewById(R.id.rlRootView);

        rlRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int heightDiff = rlRootView.getRootView().getHeight() - rlRootView.getHeight();
                    if (heightDiff > Util.dpToPx(DWAddNewPlayerActivity.this, Numerics.HUNDRED * Numerics.TWO)) {
                        // if more than 200 dp, it's probably a keyboard...
                    } else {
                        hideSystemUI();
                    }
                }
            });

        rlBottomLayout = (RelativeLayout) findViewById(R.id.rlBottomLayout);
//        llFirstPlayer = (LinearLayout) findViewById(R.id.llAddPlayer);
        mPlayersGridView = (GridView) findViewById(R.id.gridPlayers);
//        btnEditPlayer = (Button) findViewById(R.id.btnEditPlayer);
        btnAddPlayer = (Button) findViewById(R.id.btnAddPlayer);
        btnContinue = (Button) findViewById(R.id.btnContinue);
//        ivAddPlayer = (ImageView) findViewById(R.id.ivAvatar);
        tvAddPlayersMsg = (TextView) findViewById(R.id.tvAddPlayersMsg);
        tvFilterAll = (TextView) findViewById(R.id.tvFilterAll);
        tvFilterABCD = (TextView) findViewById(R.id.tvFilterABCD);
        tvFilterEFGH = (TextView) findViewById(R.id.tvFilterEFGH);
        tvFilterIJKL = (TextView) findViewById(R.id.tvFilterIJKL);
        tvFilterMNO = (TextView) findViewById(R.id.tvFilterMNO);
        tvFilterPQRS = (TextView) findViewById(R.id.tvFilterPQRS);
        tvFilterTUV = (TextView) findViewById(R.id.tvFilterTUV);
        tvFilterWXYZ = (TextView) findViewById(R.id.tvFilterWXYZ);
        llFilters = (LinearLayout) findViewById(R.id.llFilters);

//        if (isAddNewPlayer) {
//            rlBottomLayout.setVisibility(View.GONE);
////            mPlayersGridView.setVisibility(View.INVISIBLE);
//        } else {
//            rlBottomLayout.setVisibility(View.VISIBLE);
////            mPlayersGridView.setVisibility(View.VISIBLE);
//        }

//        btnEditPlayer.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.2));
        btnAddPlayer.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.2));
        btnContinue.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.2));

//        btnEditPlayer.setOnClickListener(this);
        btnAddPlayer.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
//        ivAddPlayer.setOnClickListener(this);
        tvFilterAll.setOnClickListener(this);
        tvFilterABCD.setOnClickListener(this);
        tvFilterEFGH.setOnClickListener(this);
        tvFilterIJKL.setOnClickListener(this);
        tvFilterMNO.setOnClickListener(this);
        tvFilterPQRS.setOnClickListener(this);
        tvFilterTUV.setOnClickListener(this);
        tvFilterWXYZ.setOnClickListener(this);
        tvAddPlayersMsg.setText(String.format(getString(R.string.Add_Player_Msg),
                                              sharedPrefUtil.getInteger(SharedPrefUtil.PREF_PLAYERS_LIMIT) + ""));
    }

    /**
     * Listener for activity UI fields click events
     *
     * @param v return clicked view
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnAddPlayer || v.getId() == R.id.ivAvatar) {
            if (!isAddplayerClicked) {
                isAddplayerClicked = true;
                Intent intent = new Intent(this, DWAddEditPlayerActivity.class);
                startActivityForResult(intent, Numerics.ZERO);
                new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isAddplayerClicked = false;
                        }
                    }, 1000);

            }
        } else if (v.getId() == R.id.btnContinue) {

//            if (!btnContinue.getText().toString().equals(getString(R.string.DoneEditing))) {
            Intent intent = new Intent(this, DWAllSetForGameActivity.class);
            startActivityForResult(intent, Numerics.ZERO);
//            finish();
//            } else {
//                btnContinue.setText(getString(R.string.Continue));
//                if (mPlayersAdapter.getCount() == Numerics.ZERO) {
//                    btnContinue.setVisibility(View.INVISIBLE);
//                    btnContinue.setClickable(false);
//                    btnAddPlayer.setVisibility(View.VISIBLE);
//                    btnAddPlayer.setClickable(true);
////                    btnEditPlayer.setVisibility(View.INVISIBLE);
////                    btnEditPlayer.setClickable(false);
//                } else if (mPlayersAdapter.getCount() < sharedPrefUtil.getInteger(SharedPrefUtil.PREF_PLAYERS_LIMIT)) {
//                    btnAddPlayer.setVisibility(View.VISIBLE);
//                    btnAddPlayer.setClickable(true);
////                    btnEditPlayer.setVisibility(View.VISIBLE);
////                    btnEditPlayer.setClickable(true);
//                    btnContinue.setVisibility(View.VISIBLE);
//                    btnContinue.setClickable(true);
//                } else {
//                    btnAddPlayer.setVisibility(View.INVISIBLE);
//                    btnAddPlayer.setClickable(false);
////                    btnEditPlayer.setVisibility(View.VISIBLE);
////                    btnEditPlayer.setClickable(true);
//                    btnContinue.setVisibility(View.VISIBLE);
//                    btnContinue.setClickable(true);
//                }
//            }
        } else if (v.getId() == R.id.btnEditPlayer) {

            btnContinue.setText(getString(R.string.DoneEditing));
            btnAddPlayer.setVisibility(View.INVISIBLE);
            btnAddPlayer.setClickable(false);
//            btnEditPlayer.setVisibility(View.INVISIBLE);
//            btnEditPlayer.setClickable(false);
        } else if (v.getId() == R.id.tvFilterAll || v.getId() == R.id.tvFilterABCD
                   || v.getId() == R.id.tvFilterEFGH || v.getId() == R.id.tvFilterIJKL
                   || v.getId() == R.id.tvFilterMNO || v.getId() == R.id.tvFilterPQRS
                   || v.getId() == R.id.tvFilterTUV || v.getId() == R.id.tvFilterWXYZ) {

            if (tvSelFilter != null) {
                tvSelFilter.setBackground(getResources().getDrawable(R.drawable.filter_bg_white));
            }

            tvSelFilter = (TextView) v;
            tvSelFilter.setBackground(getResources().getDrawable(R.drawable.filter_bg));
            new FilterTask(((TextView) v).getText().toString()).execute();
        }
    }

    /**
     * get the login user players list from the server
     *
     * @param tokenId  login user token id to access the server data
     * @param parentId login user id
     * @throws Exception thrown for network/response  issues
     */
    private void callGetChildAPI(String tokenId, String parentId) throws Exception {
        playersList = null;
        if (NetworkUtil.checkInternetConnection(this)) {
            showProgressDialog(getString(R.string.Please_Wait));
            ApiClient.getApiInterface(this).getChildAPI(JSONConstants.AUTHORIZATION_BEARER + tokenId, getString(R.string.gameID), parentId).enqueue(new Callback<List<PlayerDO>>() {
                    @Override
                    public void onResponse(Call<List<PlayerDO>> call, Response<List<PlayerDO>> response) {
                        dismissProgressDialog();
                        try {
                            if (response.body() != null) {
                                playersList = response.body();
                                Logger.info("GetChild Success body", playersList.toString());

                                intUI(false);
                                updateUI();
//                            }

                            } else {
                                if (response.errorBody() != null) {
                                    if (response.code() == Constants.RESPONSE_CODE_UNAUTHORIZED) {
                                        NetworkUtil.callReAuthenticationAPI(DWAddNewPlayerActivity.this, DWAddNewPlayerActivity.this, true);
                                    }
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    Logger.info("GetChild Error body", jsonObject.getString(JSONConstants.MESSAGE));
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (playersList != null && playersList.size() > Numerics.NINE) {
                            llFilters.setVisibility(View.VISIBLE);
                        } else {
                            llFilters.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PlayerDO>> call, Throwable t) {
                        dismissProgressDialog();
                        Logger.info("GetChild", "onFailure: " + t.toString());
                        t.printStackTrace();

                        if (playersList != null && playersList.size() > (Numerics.TEN + Numerics.TWO)) {
                            llFilters.setVisibility(View.VISIBLE);
                        } else {
                            llFilters.setVisibility(View.GONE);
                        }
                    }
                });
        } else {
            getStoredPlayersList();
        }
    }

    private void getStoredPlayersList() {
        // continue with last login info in offline
        String jsonString = Util.getPlayersListFromFile(this);
        if (!TextUtils.isEmpty(jsonString)) {
            Type listType = new TypeToken<List<PlayerDO>>() {
                }.getType();
            playersList = new Gson().fromJson(jsonString, listType);
            updateUI();
            if (playersList != null && playersList.size() > (Numerics.TEN + Numerics.TWO)) {
                llFilters.setVisibility(View.VISIBLE);
            } else {
                llFilters.setVisibility(View.GONE);
            }
        } else {
            Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet), getString(R.string.Ok),
                           null);
        }
    }

    private void updateUI() {
        mPlayersAdapter.refreshData((ArrayList<PlayerDO>) playersList);

//                            if (!btnContinue.getText().toString().equals(getString(R.string.DoneEditing))) {
        if (mPlayersAdapter.getCount() > Numerics.ZERO) {
            if (playersList.size() < sharedPrefUtil.getInteger(SharedPrefUtil.PREF_PLAYERS_LIMIT)) {
//                                        btnEditPlayer.setVisibility(View.VISIBLE);
//                                        btnEditPlayer.setClickable(true);
                btnContinue.setVisibility(View.VISIBLE);
                btnContinue.setClickable(true);
                btnAddPlayer.setVisibility(View.VISIBLE);
                btnAddPlayer.setClickable(true);
            } else {
//                                        btnEditPlayer.setVisibility(View.VISIBLE);
//                                        btnEditPlayer.setClickable(true);
                btnContinue.setVisibility(View.VISIBLE);
                btnContinue.setClickable(true);
                btnAddPlayer.setVisibility(View.INVISIBLE);
                btnAddPlayer.setClickable(false);
            }
        } else {
            PlayerUtil.setSelectedPlayer(DWAddNewPlayerActivity.this, null);
//                                    btnEditPlayer.setVisibility(View.INVISIBLE);
//                                    btnEditPlayer.setClickable(false);
            btnContinue.setVisibility(View.INVISIBLE);
            btnContinue.setClickable(false);
            btnAddPlayer.setVisibility(View.VISIBLE);
            btnAddPlayer.setClickable(true);
        }
    }

    /**
     * Call Back method  to get the Message form other Activity
     *
     * @param requestCode request code
     * @param resultCode  result code
     * @param data        intent data from form other Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                callGetChildAPI(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN), sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID));
            } catch (Exception e) {
                e.printStackTrace();
                getStoredPlayersList();
            }
        }
    }

    public class FilterTask extends AsyncTask<Void, Void, List<PlayerDO>> {

        private String filterName;

        public FilterTask(String filterName) {
            this.filterName = filterName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showProgressDialog(null);
        }

        @Override
        protected List<PlayerDO> doInBackground(Void... params) {

            if (filterName.replace("•", ",").split(",").length == Numerics.ONE) {
                return playersList;
            } else {

                String filters[] = filterName.replace("•", ",").split(",");

                List<PlayerDO> tempList = new ArrayList<>();

                for (int i = 0; i < playersList.size(); i++) {

                    for (int j = 0; j < filters.length; j++) {

                        if (playersList.get(i).getFirstName().toLowerCase().contains(filters[j].toLowerCase())) {
                            tempList.add(playersList.get(i));
                        }
                    }
                }

                return tempList;
            }
        }

        @Override
        protected void onPostExecute(List<PlayerDO> playerDOs) {
            super.onPostExecute(playerDOs);
            dismissProgressDialog();
            mPlayersAdapter.refreshData((ArrayList<PlayerDO>) playerDOs);
        }
    }

    @Override
    protected void didNavigatesToMainMenu() {

    }

    @Override
    public void onResponse(Object object) {

        try {
            if (sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_LOGIN_USER_STATUS)) {
                callGetChildAPI(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN), sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Object error) {

    }
}