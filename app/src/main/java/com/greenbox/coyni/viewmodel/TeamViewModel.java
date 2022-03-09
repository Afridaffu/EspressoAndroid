package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.team.TeamDeleteModel;
import com.greenbox.coyni.model.team.TeamInfoAddModel;
import com.greenbox.coyni.model.team.TeamRequest;
import com.greenbox.coyni.model.team.TeamResponseModel;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamViewModel extends AndroidViewModel {
    public TeamViewModel(@NonNull Application application) {
        super(application);
    }
    private MutableLiveData<TeamResponseModel> teamMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TeamDeleteModel> teamDelMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TeamInfoAddModel> teamAddMutableLiveData = new MutableLiveData<TeamInfoAddModel>();

    public MutableLiveData<TeamResponseModel> getTeamMutableLiveData() {
        return teamMutableLiveData;
    }
    public MutableLiveData<TeamDeleteModel> getTeamDelMutableLiveData() {
        return teamDelMutableLiveData;
    }
    public MutableLiveData<TeamInfoAddModel> getTeamAddMutableLiveData() {
        return teamAddMutableLiveData;
    }

    public void getTeamInfo(TeamRequest teamRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TeamResponseModel> mCall = apiService.getTeamData(teamRequest);
            mCall.enqueue(new Callback<TeamResponseModel>() {
                @Override
                public void onResponse(Call<TeamResponseModel> call, Response<TeamResponseModel> response) {
                    Log.d("TeamInfo", response.toString());
                    try {
                        if (response.isSuccessful()) {
                            TeamResponseModel obj = response.body();

                            teamMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<TeamResponseModel>() {}.getType();
                            TeamResponseModel errorResponse = gson.fromJson(response.errorBody().string(), type);
                            teamMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        teamMutableLiveData.setValue(null);
                    }
                }
                @Override
                public void onFailure(Call<TeamResponseModel> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    teamMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateTeamInfo(TeamRequest teamRequest,int teamMemberId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TeamResponseModel> mCall = apiService.updateTeamData(teamRequest,teamMemberId);
            mCall.enqueue(new Callback<TeamResponseModel>() {
                @Override
                public void onResponse(Call<TeamResponseModel> call, Response<TeamResponseModel> response) {
                    Log.d("UpdateTeamInfo", response.toString());
                    try {
                        if (response.isSuccessful()) {
                            TeamResponseModel obj = response.body();

                            teamMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<TeamResponseModel>() {}.getType();
                            TeamResponseModel errorResponse = gson.fromJson(response.errorBody().string(), type);
                            teamMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        teamMutableLiveData.setValue(null);
                    }
                }
                @Override
                public void onFailure(Call<TeamResponseModel> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    teamMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteTeam(Integer teamMemberId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TeamDeleteModel> mCall = apiService.deleteTeam(teamMemberId);
            mCall.enqueue(new Callback<TeamDeleteModel>() {
                @Override
                public void onResponse(Call<TeamDeleteModel> call, Response<TeamDeleteModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            TeamDeleteModel obj = response.body();
                            teamDelMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<APIError>() {
                            }.getType();
                            APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
                            apiErrorMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        apiErrorMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<TeamDeleteModel> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addTeam(TeamRequest teamRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TeamInfoAddModel> mCall = apiService.addTeamMember(teamRequest);
            mCall.enqueue(new Callback<TeamInfoAddModel>() {
                @Override
                public void onResponse(Call<TeamInfoAddModel> call, Response<TeamInfoAddModel> response) {
                    try {
                        Log.d("TeamInfo",response.body().getStatus());
                        if (response.isSuccessful()) {
                            TeamInfoAddModel obj = response.body();
                            teamAddMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<TeamInfoAddModel>() {
                            }.getType();
                            TeamInfoAddModel errorResponse = gson.fromJson(response.errorBody().string(), type);
                            teamAddMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        teamAddMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<TeamInfoAddModel> call, Throwable t) {
                    // Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    teamAddMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
