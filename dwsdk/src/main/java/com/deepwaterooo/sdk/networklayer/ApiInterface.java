package com.deepwaterooo.sdk.networklayer;

import com.deepwaterooo.sdk.appconfig.JSONConstants;
import com.deepwaterooo.sdk.beans.AppUpdatesDO;
import com.deepwaterooo.sdk.beans.AvatarsDO;
import com.deepwaterooo.sdk.beans.LoginUserDO;
import com.deepwaterooo.sdk.beans.PlayerDO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Class used to define network requests Apis endpoints
 * using special retrofit annotations to encode details about the parameters and request method.
 * the return value is always a parameterized Call<T> object
 */
// 注册,登录,邮箱验证,登出 等等相关网络请求接口设计(我需要把游戏进度保存到网络吗?单人游戏,应该大可不必,保存用户客户端设备应该就可以了)
// 我也没有那么大的网络空间来帮每个用户保存好多个不同的游戏进展呀......
public interface ApiInterface {

    /**
     * Login API
     *
     * @param identifier user registered email address
     * @param password   user password
     * @param game       user game id
     * @return
     */
    @FormUrlEncoded
    @POST("user/v2/login")
    Call<LoginUserDO> loginUserAPI(@Field(JSONConstants.IDENTIFIER) String identifier,
                                   @Field(JSONConstants.PASSWORD) String password,
                                   @Field(JSONConstants.GAME) String game);

    /**
     * Sign Up API
     *
     * @param firstName user first name
     * @param lastName  user last name
     * @param email     user email address
     * @param password  user password to login
     * @param role      user role as parent/child
     * @param game      user game id
     * @param terms     terms and conditions
     * @param privacy   privacy
     * @return
     */
    @FormUrlEncoded
    @POST("user/register")
    Call<ResponseBody> signUPUserAPI(@Field(JSONConstants.FIRST_NAME) String firstName,
                                     @Field(JSONConstants.LAST_NAME) String lastName,
                                     @Field(JSONConstants.EMAIL) String email,
                                     @Field(JSONConstants.PASSWORD) String password,
                                     @Field(JSONConstants.ROLE) String role,
                                     @Field(JSONConstants.GAME) String game,
                                     @Field(JSONConstants.TERMS) String terms,
                                     @Field(JSONConstants.PRIVACY) String privacy,
                                     @Field(JSONConstants.STAY_UP_TO_DATE) String stayUpToDate);

    /**
     * to verify the user email address
     *
     * @param email user registered email address
     * @return
     */
    @FormUrlEncoded
    @POST("user/validate/email")
    Call<ResponseBody> verifyEmailAPI(@Field(JSONConstants.EMAIL) String email);


    /**
     * to resend the email address
     *
     * @param email user registered email address
     * @return
     */
    @FormUrlEncoded
    @POST("user/resendemail")
    Call<ResponseBody> resendEmailAPI(@Field(JSONConstants.EMAIL) String email);


    /**
     * to get password using registered email address
     *
     * @param email user registered email address
     * @return
     */
    @FormUrlEncoded
    @POST("user/forgot-password")
    Call<ResponseBody> forgotPasswordAPI(@Field(JSONConstants.EMAIL) String email);

    // /**
    //  * API used to get terms and conditions
    //  *
    //  * @return
    //  */
    // @GET("cms/v2/listCmsbyCategory/terms/{role}")
    // Call<PrivacyPolicyDO> termsAndConditionsAPI(/*@Header(JSONConstants.AUTHORIZATION_TOKEN) String authorization,*/
    //     @Path("role") String role);
    // /**
    //  * API used to get terms and conditions
    //  *
    //  * @return
    //  */
    // @GET("cms/v2/listCmsbyCategory/terms/{role}")
    // Call<PrivacyPolicyDO> termsAndConditionsWithTokenAPI(@Header(JSONConstants.AUTHORIZATION_TOKEN) String authorization,
    //                                                      @Path("role") String role);

    /**
     * API used to submit terms and conditions
     *
     * @return
     */
    @FormUrlEncoded
    @PUT("user/{parent_id}/cms/terms")
    Call<ResponseBody> submitTermsAndConditionsAPI(@Header(JSONConstants.AUTHORIZATION_TOKEN) String authorization,
                                                   @Path("parent_id") String parentId,
                                                   @Field(JSONConstants.VERSION) String version);

//    /**
//     * API used to get user Privacy policy description
//     *
//     * @return
//     */
//    @GET("cms/v2/listCmsbyCategory/privacy/{role}")
//    Call<PrivacyPolicyDO> privacyPolicyAPI(/*@Header(JSONConstants.AUTHORIZATION_TOKEN) String authorization,*/
//        @Path("role") String role);
//
//    /**
//     * API used to get user Privacy policy description
//     *
//     * @return
//     */
//    @GET("cms/v2/listCmsbyCategory/privacy/{role}")
//    Call<PrivacyPolicyDO> privacyPolicyWithTokenAPI(@Header(JSONConstants.AUTHORIZATION_TOKEN) String authorization,
//                                                    @Path("role") String role);


    /**
     * API used to submit Privacy policy description
     *
     * @return
     */
    @FormUrlEncoded
    @PUT("user/{parent_id}/cms/privacy")
    Call<ResponseBody> submitPrivacyPolicyAPI(@Header(JSONConstants.AUTHORIZATION_TOKEN) String authorization,
                                              @Path("parent_id") String parentId,
                                              @Field(JSONConstants.VERSION) String version);

    /**
     * API used to Re-Authentication login user using existing token and parent id
     *
     * @return
     */
    @FormUrlEncoded
    @POST("user/generate/new-token")
    Call<ResponseBody> ReAuthenticationAPI(@Field(JSONConstants.REFRESH_TOKEN) String refreshToken);

    //base_url/“file_name”/file/uploads3
    @FormUrlEncoded
    @POST("{file_name}/file/uploads3")
    Call<ResponseBody> uploadFile(@Header(JSONConstants.AUTHORIZATION_TOKEN) String authorization,
                                  @Path("file_name") String fileName,
                                  @Field(JSONConstants.CONTENTS) String imgbase64);

// removed because of to make dynamic URL
//    @GET("https://squarepanda-dev.s3.amazonaws.com/games/{file_name}")
//    Call<ResponseBody> downloadFile(@Path("file_name") String fileName);

    /**
     * API used to download a file from given URL
     *
     * @param fileUrl a url to download a file
     * @return
     */
    @GET
    Call<ResponseBody> downloadFileWithURL(@Url String fileUrl);

// 我应该不需要下面这个东西
    @FormUrlEncoded
    @POST("/games/v2/appUpdates")
    Call<AppUpdatesDO> appUpdateAPI(@Field(JSONConstants.APP_USER_ID) String userId,
                                    @Field(JSONConstants.GAME_ID) String gameId,
                                    @Field(JSONConstants.SDK_VERSION) String sdkVersion,
                                    @Field(JSONConstants.GAME_VERSION) String gameVersion,
                                    @Field(JSONConstants.PLATFORM) String plotform);

    /**
     * API used to submit Privacy policy description
     *
     * @return
     */
    @GET("avatar/{parent_id}/get-avatars")
    Call<List<AvatarsDO>> getGenericAvatars(@Header(JSONConstants.AUTHORIZATION_TOKEN) String authorization,
                                            @Path("parent_id") String parentId);

    /**
     * API used to Adding child for game
     *
     * @param firstName   user first name
     * @param dateofBirth user date of birth
     * @param gender      user gender
     * @param avatar      user avatar
     * @param profileURL  user profile image URL
     * @param imgbase64   user image in base 64
     * @param game        user game id
     * @return
     */
    @FormUrlEncoded
    @POST("user/{parent_id}/child/")
    Call<ResponseBody> addChildAPI(@Header(JSONConstants.AUTHORIZATION_TOKEN) String authorization,
                                   @Path("parent_id") String parentId,
                                   @Field(JSONConstants.FIRST_NAME) String firstName,
                                   @Field(JSONConstants.DATE_OF_BIRTH) String dateofBirth,
                                   @Field(JSONConstants.GENDER) String gender,
                                   @Field(JSONConstants.AVATAR) String avatar,
                                   @Field(JSONConstants.PROFILE_URL) String profileURL,
                                   @Field(JSONConstants.IMG_BASE_64) String imgbase64,
                                   @Field(JSONConstants.GAME) String game);

    /**
     * API used to editing child for game
     *
     * @param firstName   user first name
     * @param dateofBirth user date of birth
     * @param gender      user gender
     * @param avatar      user avatar
     * @param profileURL  user profile image URL
     * @param imgbase64   user image in base 64
     * @return
     */
    @FormUrlEncoded
    @PUT("user/{parent_id}/child/{child_id}/edit")
    Call<ResponseBody> editChildAPI(@Header(JSONConstants.AUTHORIZATION_TOKEN) String authorization,
                                    @Path("parent_id") String parentId,
                                    @Path("child_id") String childId,
                                    @Field(JSONConstants.FIRST_NAME) String firstName,
                                    @Field(JSONConstants.DATE_OF_BIRTH) String dateofBirth,
                                    @Field(JSONConstants.GENDER) String gender,
                                    @Field(JSONConstants.AVATAR) String avatar,
                                    @Field(JSONConstants.PROFILE_URL) String profileURL,
                                    @Field(JSONConstants.IMG_BASE_64) String imgbase64);

    /**
     * API used to delete child
     *
     * @return
     */
    @DELETE("user/{parent_id}/child/{child_id}")
    Call<ResponseBody> deleteChildAPI(@Header(JSONConstants.AUTHORIZATION_TOKEN) String authorization,
                                      @Path("parent_id") String parentId,
                                      @Path("child_id") String childId);

    /**
     * Api used to update the Teacher student details
     *
     * @param authorization   authorization token
     * @param teacherId       teacher id
     * @param studentId       student id
     * @param firstName       user first name
     * @param lastName        user last name
     * @param dateofBirth     user Date of birth
     * @param gender          user gender
     * @param grade           user grade
     * @param parentEmail     user parent email
     * @param iep             user IEP
     * @param language        user known languages
     * @param iep_description user IEP description
     * @param imgbase64       user image in base 64
     * @param avatar          user avatar
     * @param profileURL      user profile image URL
     * @return
     */

    @FormUrlEncoded
    @PUT("user/v2/{teacherId}/student/{studentId}/edit")
    Call<ResponseBody> editStudentAPI(@Header(JSONConstants.AUTHORIZATION_TOKEN) String authorization,
                                      @Path("teacherId") String teacherId,
                                      @Path("studentId") String studentId,
                                      @Field(JSONConstants.FIRST_NAME) String firstName,
                                      @Field(JSONConstants.LAST_NAME) String lastName,
                                      @Field(JSONConstants.DATE_OF_BIRTH) String dateofBirth,
                                      @Field(JSONConstants.GENDER) String gender,
                                      @Field(JSONConstants.PARENT_EMAIL) String parentEmail,
                                      @Field(JSONConstants.GRADE) String grade,
                                      @Field(JSONConstants.STUDENT_ID) String student_id,
                                      @Field(JSONConstants.IEP) String iep,
                                      @Field(JSONConstants.LANGUAGE) String language,
                                      @Field(JSONConstants.IEP_DESCRIPTION) String iep_description,
                                      @Field(JSONConstants.AVATAR) String avatar,
                                      @Field(JSONConstants.PROFILE_URL) String profileURL,
                                      @Field(JSONConstants.IMG_BASE_64) String imgbase64);

    /**
     * API used to to get child/player details
     *
     * @return
     */
    @GET("user/{parent_id}/child")
    Call<List<PlayerDO>> getChildAPI(@Header(JSONConstants.AUTHORIZATION_TOKEN) String authorization,
                                     @Header(JSONConstants.GAME_ID) String gameId,
                                     @Path("parent_id") String parentId);


}