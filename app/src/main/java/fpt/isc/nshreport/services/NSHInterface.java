package fpt.isc.nshreport.services;

import fpt.isc.nshreport.models.DebtReport;
import fpt.isc.nshreport.models.objectParse.BaseResponse;
import fpt.isc.nshreport.models.objectParse.CheckVersion;
import fpt.isc.nshreport.models.objectParse.DebtNumber;
import fpt.isc.nshreport.models.objectParse.DebtReportAPI;
import fpt.isc.nshreport.models.objectParse.ImportDetail;
import fpt.isc.nshreport.models.objectParse.ImportList;
import fpt.isc.nshreport.models.objectParse.NotificationCount;
import fpt.isc.nshreport.models.objectParse.NotificationList;
import fpt.isc.nshreport.models.objectParse.OptionsList;
import fpt.isc.nshreport.models.objectParse.ProductList;
import fpt.isc.nshreport.models.objectParse.ReportDetail;
import fpt.isc.nshreport.models.objectParse.ReportList;
import fpt.isc.nshreport.models.objectParse.ShiftList;
import fpt.isc.nshreport.models.objectParse.ShiftOpen;
import fpt.isc.nshreport.models.objectParse.PriceList;
import fpt.isc.nshreport.models.objectParse.TankList;
import fpt.isc.nshreport.models.objectParse.UserList;
import fpt.isc.nshreport.models.objectParse.UserLogin;
import fpt.isc.nshreport.models.objectParse.stationDetail;
import fpt.isc.nshreport.models.posts.UserPost;
import fpt.isc.nshreport.models.posts.reportAdd;
import fpt.isc.nshreport.models.posts.reportDebt;
import fpt.isc.nshreport.models.posts.reportUpdate;
import fpt.isc.nshreport.models.posts.shiftCreate;
import fpt.isc.nshreport.models.posts.shiftUpdate;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by PhamTruong on 29/05/2017.
 */

public interface NSHInterface {
    //check version
    @GET("api/v1/check-version")
    Call<CheckVersion> checkVersion(@Query("platform") String platform,@Query("imei") String imei);
    //Observable<NotificationList> notifyList(@Header("Api-Token") String header,@Query("page") int page);

    @Multipart
    @POST("api/v1/upload/image")
    Call<BaseResponse> uploadDocument3(@Header("Api-Token") String header,@Part("description") RequestBody description, @Part MultipartBody.Part file);

    //get status options
    @GET("api/v1/report/status-options")
    Observable<OptionsList> statusList(@Header("Api-Token") String header);

    //Call login
    @POST("api/v1/login")
    Observable<UserLogin> callLogin(@Body UserPost userPost);

    //list report
    @GET("api/v1/report")
    Observable<ReportList> reportList(@Header("Api-Token") String header,@Query("page") int page,@Query("order_by") String order_by,@Query("status") String status,@Query("shift_status") String shift_status);

    //Report detail
    @GET("api/v1/report/{id}")
    Observable<ReportDetail> reportDetail(@Header("Api-Token") String header, @Path(value = "id",encoded = false)int id);

    //update report
    @PUT("api/v1/report/{id}")
    Observable<BaseResponse> reportUpdate(@Header("Api-Token") String header, @Path(value = "id",encoded = false)int id, @Body reportUpdate data);

    //list report
    @GET("api/v1/station/{id}")
    Observable<stationDetail> stationDetail(@Header("Api-Token") String header, @Path(value = "id",encoded = false)String id);

    //add report
    @POST("api/v1/report")
    Observable<BaseResponse> reportAdd(@Header("Api-Token") String header,@Body reportAdd data);

    //add shift
    @POST("api/v1/report")
    Observable<BaseResponse> shiftAdd(@Header("Api-Token") String header,@Body shiftCreate data);

    //update shift
    @PUT("api/v1/report/{id}")
    Observable<BaseResponse> shiftUpdate(@Header("Api-Token") String header, @Path(value = "id",encoded = false)int id, @Body shiftUpdate data);

    //get report before
    @GET("api/v1/report/before")
    Observable<ShiftOpen> reportBefore(@Header("Api-Token") String header);

    //get report open
    @GET("api/v1/report/open")
    Observable<ShiftOpen> reportOpen(@Header("Api-Token") String header);

    //get shift list
    @GET("api/v1/shift")
    Observable<ShiftList> shiftList(@Header("Api-Token") String header);

    //get staff list
    @GET("api/v1/user")
    Observable<UserList> userList(@Header("Api-Token") String header);

    //get price list
    @GET("api/v1/table-price")
    Observable<PriceList> priceList(@Header("Api-Token") String header);

    //get tank list
    @GET("api/v1/tank")
    Observable<TankList> tankList(@Header("Api-Token") String header);

    //get noitification list
    @GET("api/v1/notification")
    Observable<NotificationList> notifyList(@Header("Api-Token") String header,@Query("page") int page);

    //get noitification count
    @GET("api/v1/notification/counter")
    Observable<NotificationCount> notifyCount(@Header("Api-Token") String header);

    //approve import
    @PUT("api/v1/notification/reset-counter")
    Observable<BaseResponse> resetNotifyCount(@Header("Api-Token") String header);

    //approve import
    @PUT("api/v1/notification/readed/{id}")
    Observable<BaseResponse> updateReadNotification(@Header("Api-Token") String header, @Path(value = "id",encoded = false)int id);

    //get status options import
    @GET("/api/v1/inventory/workflow-status")
    Observable<OptionsList> statusListImport(@Header("Api-Token") String header);

    //list import
    @GET("api/v1/inventory/workflow")
    Observable<ImportList> importList(@Header("Api-Token") String header, @Query("page") int page,@Query("order_by") String order_by,@Query("status") String status);

    //datail import
    @GET("api/v1/inventory/workflow/{id}")
    Observable<ImportDetail> importDetail(@Header("Api-Token") String header, @Path(value = "id",encoded = false)int id);

    //approve import
    @PUT("api/v1/inventory/workflow/{id}/success")
    Observable<BaseResponse> approveImport(@Header("Api-Token") String header, @Path(value = "id",encoded = false)int id);

    //product list, query string yyyy-MM-dd
    @GET("api/v1/report-revenue/products")
    Observable<ProductList> productList(@Header("Api-Token") String header, @Query("report_date") String date);

    @GET("api/v1/report-revenue/products")
    Observable<ProductList> productListStartEnd(@Header("Api-Token") String header, @Query("start_at") String startDate, @Query("end_at") String endDate);

    //debt
    @GET("api/v1/report-revenue/products")
    Observable<DebtNumber> stationDebt(@Header("Api-Token") String header);

    //debt add
    @POST("api/v1/cash-deposit-slip")
    Observable<BaseResponse> debtReportAdd(@Header("Api-Token") String header,@Body reportDebt data);

    //debt report
    @GET("api/v1/cash-deposit-slip")
    Observable<DebtReportAPI> debtReports(@Header("Api-Token") String header,@Query("page") int page, @Query("order_by") String order);

}
