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
import com.greenbox.coyni.model.team.TeamGetDataModel;
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

    private MutableLiveData<TeamResponseModel> teamRetrieveMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TeamInfoAddModel> teamDelMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TeamInfoAddModel> teamAddMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TeamInfoAddModel> teamUpdateMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TeamInfoAddModel> teamCancelMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TeamGetDataModel> teamGetMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<TeamResponseModel> getTeamRetrieveMutableLiveData() {
        return teamRetrieveMutableLiveData;
    }

    public MutableLiveData<TeamInfoAddModel> getTeamDelMutableLiveData() {
        return teamDelMutableLiveData;
    }

    public MutableLiveData<TeamInfoAddModel> getTeamAddMutableLiveData() {
        return teamAddMutableLiveData;
    }

    public MutableLiveData<TeamInfoAddModel> getTeamUpdateMutableLiveData() {
        return teamUpdateMutableLiveData;
    }

    public MutableLiveData<TeamInfoAddModel> getTeamCancelMutableLiveData() {
        return teamCancelMutableLiveData;
    }

    public MutableLiveData<TeamGetDataModel> getTeamGetMutableLiveData() {
        return teamGetMutableLiveData;
    }

    public void retrieveTeamInfo(TeamRequest teamRequest) {
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
                            teamRetrieveMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<TeamResponseModel>() {
                            }.getType();
                            TeamResponseModel errorResponse = gson.fromJson(response.errorBody().string(), type);
                            teamRetrieveMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        teamRetrieveMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<TeamResponseModel> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    teamRetrieveMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateTeamMember(TeamRequest teamRequest, int teamMemberId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TeamInfoAddModel> mCall = apiService.updateTeamMember(teamRequest, teamMemberId);
            mCall.enqueue(new Callback<TeamInfoAddModel>() {
                @Override
                public void onResponse(Call<TeamInfoAddModel> call, Response<TeamInfoAddModel> response) {
                    Log.d("UpdateTeamInfo", response.toString());
                    try {
                        if (response.isSuccessful()) {
                            TeamInfoAddModel obj = response.body();

                            teamUpdateMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<TeamInfoAddModel>() {
                            }.getType();
                            TeamInfoAddModel errorResponse = gson.fromJson(response.errorBody().string(), type);
                            teamUpdateMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        teamUpdateMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<TeamInfoAddModel> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    teamUpdateMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteTeamMember(Integer teamMemberId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TeamInfoAddModel> mCall = apiService.deleteTeamMember(teamMemberId);
            mCall.enqueue(new Callback<TeamInfoAddModel>() {
                @Override
                public void onResponse(Call<TeamInfoAddModel> call, Response<TeamInfoAddModel> response) {
                    try {
                        Log.d("RemoveTeamInfo", response.toString());
                        if (response.isSuccessful()) {
                            TeamInfoAddModel obj = response.body();
                            teamDelMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<TeamInfoAddModel>() {
                            }.getType();
                            TeamInfoAddModel errorResponse = gson.fromJson(response.errorBody().string(), type);
                            teamDelMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        teamDelMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<TeamInfoAddModel> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    teamDelMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cancelTeamMember(Integer teamMemberId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TeamInfoAddModel> mCall = apiService.cancelTeamMember(teamMemberId);
            mCall.enqueue(new Callback<TeamInfoAddModel>() {
                @Override
                public void onResponse(Call<TeamInfoAddModel> call, Response<TeamInfoAddModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            TeamInfoAddModel obj = response.body();
                            teamCancelMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<TeamInfoAddModel>() {
                            }.getType();
                            TeamInfoAddModel errorResponse = gson.fromJson(response.errorBody().string(), type);
                            teamCancelMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        teamCancelMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<TeamInfoAddModel> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    teamCancelMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getTeamMember(Integer teamMemberId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TeamGetDataModel> mCall = apiService.getTeamMember(teamMemberId);
            mCall.enqueue(new Callback<TeamGetDataModel>() {
                @Override
                public void onResponse(Call<TeamGetDataModel> call, Response<TeamGetDataModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            TeamGetDataModel obj = response.body();
                            teamGetMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<TeamGetDataModel>() {
                            }.getType();
                            TeamGetDataModel errorResponse = gson.fromJson(response.errorBody().string(), type);
                            teamGetMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        teamGetMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<TeamGetDataModel> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    teamGetMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addTeamMember(TeamRequest teamRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TeamInfoAddModel> mCall = apiService.addTeamMember(teamRequest);
            mCall.enqueue(new Callback<TeamInfoAddModel>() {
                @Override
                public void onResponse(Call<TeamInfoAddModel> call, Response<TeamInfoAddModel> response) {
                    try {
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
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    teamAddMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
