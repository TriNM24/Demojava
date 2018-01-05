package fpt.isc.nshreport.utilities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.SpinnerObject;
import fpt.isc.nshreport.models.customView.BadgeDrawable;
import fpt.isc.nshreport.models.customView.CallBackConfirmDialog;
import fpt.isc.nshreport.models.customView.CallBackInformDialog;
import fpt.isc.nshreport.models.objectParse.OptionsList;

/**
 * Created by PhamTruong on 30/05/2017.
 */

public class AppUtilities {
    public static final int ALBUM_PHOTOS = 111;
    public static final int REQUEST_CAMERA = 112;
    public static final int MEDIA_TYPE_IMAGE = 113;

    public AppUtilities() {
    }

    /**
     * Convert long number to string type with separator
     *
     * @param longNumber
     * @return
     */
    public static String longTypeTextFormat(long longNumber) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(longNumber);
    }

    /**
     * Convert integer number to string type with separator
     *
     * @param integerNumber
     * @return
     */
    public static String integerTypeTextFormat(int integerNumber) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(integerNumber);
    }

    public static String LongTypeTextFormat(long Number) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(Number);
    }

    public static String StringNumerTextFormat(String number)
    {
        String rs = "";
        if(number.length() > 3) {
            String temp;
            temp = number.substring(number.length() - 3, number.length());
            rs = temp;
            number = number.substring(0, number.length() - 3);

            while (number.length() > 0) {
                if (number.length() > 2) {
                    temp = number.substring(number.length() - 3, number.length());
                    rs = temp + "," + rs;
                    number = number.substring(0, number.length() - 3);
                }else
                {
                    rs = number + "," + rs;
                    number = "";
                }
            }
        }else
        {
            return number;
        }
        return rs;
    }

    public static String ConvertStringDateToStringDate(String inputDate, String inputFormat, String outputFormat) {
        String result = "";
        Date converDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat(inputFormat);
        SimpleDateFormat df2 = new SimpleDateFormat(outputFormat);
        try {
            converDate = df.parse(inputDate);
            result = df2.format(converDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getStatus(List<OptionsList.data> data, String id) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).id.equals(id)) {
                return data.get(i).name;
            }
        }
        return "";
    }

    /**
     * Convert String to Date
     *
     * @param strDate
     * @return
     */
    public Date convertStringToDate(String strDate) {
        Date convertDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            convertDate = df.parse(strDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return convertDate;
    }

    /**
     * Convert Date to String
     *
     * @param date
     * @return
     */
    public String convertDateToString(Date date) {
        String convertString = "";
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            convertString = df.format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return convertString;
    }

    /**
     * Convert Milliseconds String to Date Time
     *
     * @param strMilliseconds
     * @return
     */
    public String convertStringToDateTime(String strMilliseconds) {
        String convertString = "";
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        try {
            Long l = Long.parseLong(strMilliseconds);
            Date date = new Date(l);
            convertString = df.format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return convertString;
    }

    /**
     * Rounded corner image view
     *
     * @param raw
     * @param round
     * @return
     */
    public Bitmap roundCornerImage(Bitmap raw, float round) {
        int width = raw.getWidth();
        int height = raw.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#000000"));

        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, round, round, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(raw, rect, rect, paint);

        return result;
    }

    /**
     * Get image path
     *
     * @param uri
     * @return
     */
    private String getPath2(Context context, Uri uri) {
        String path = "";
        try {
            if (uri == null) {
                return null;
            }
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor == null) {
                path = uri.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return path;
    }

    /**
     * Get image path
     *
     * @param contentUri
     * @return
     */
    public String getRealPathUrlImage(Context context, Uri contentUri) {
        String result = "";
        try {
            if (Build.VERSION.SDK_INT >= 19) {
                result = getRealPathFromURI_API19(context, contentUri);
            } else {
                result = getRealPathFromURI_API11to18(context, contentUri);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Get image path with API 19
     *
     * @param uri
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    private String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        try {
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        } catch (Exception e) {
            filePath = getPath2(context, uri);
        }
        return filePath;
    }

    /**
     * Get image path with API 11 to 18
     *
     * @param contentUri
     * @return
     */
    private String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String filePath = "";
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                filePath = cursor.getString(column_index);
            }
        } catch (Exception e) {
            filePath = getPath2(context, contentUri);
        }
        return filePath;
    }

    /**
     * Create directory and file picture
     *
     * @param type
     * @return
     */
    public File getOutputMediaFile(Context context, int type) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Pictures");
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Toast.makeText(context, "Không thể tạo thư mục!", Toast.LENGTH_LONG).show();
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + System.currentTimeMillis() + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    public static final void setErrorToSpinner(Spinner mySpinner, String error, boolean valid) {
        if (valid) {
            TextView errorText = (TextView) mySpinner.getSelectedView();
            errorText.setError(error);
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(error);//changes the selected item text to this
        }
    }

    public static void setSelectionViaKey(String key, Spinner spinner) {
        ArrayAdapter<SpinnerObject> adapter = (ArrayAdapter<SpinnerObject>) spinner.getAdapter();
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            if (key.trim().equals(adapter.getItem(i).getKey())) {
                // exist = true;
                spinner.setSelection(i);
                break;
            }
        }
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    /**
     * show infirm message
     */
    public static void showInform(Context context,String title ,String message, String text_button,int msgType, CallBackInformDialog callback) {

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context,msgType);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.setConfirmText(text_button);
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                callback.DiaglogPositive();
            }
        });
        sweetAlertDialog.setCanceledOnTouchOutside(false);
        sweetAlertDialog.show();
    }

    /**
     * show conform message
     */
    public static void showConfirm(Context context,String title, String message, String text_pos, String text_neg, int msgType,CallBackConfirmDialog callback) {

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context,msgType);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.setConfirmText(text_pos);
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                callback.DiaglogPositive();
            }
        });
        sweetAlertDialog.setCancelText(text_neg);
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                callback.DiaglogNegative();
            }
        });
        sweetAlertDialog.setCanceledOnTouchOutside(false);
        sweetAlertDialog.show();
    }

    public static void showChooseCamera(Activity activity,CallBackConfirmDialog callback)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.diaglog_choose_camera, null);
        dialogBuilder.setView(dialogView);

        LinearLayout btnAlbum = (LinearLayout) dialogView.findViewById(R.id.btn_album);
        LinearLayout btnCamera = (LinearLayout) dialogView.findViewById(R.id.btn_camera);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();

        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callback.DiaglogNegative();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callback.DiaglogPositive();
            }
        });


    }

    /**
     * Show progress diaglog
     */
    public static ProgressDialog showProcessing(Context context,String message)
    {
        ProgressDialog rs = new ProgressDialog(context);
        rs.setCancelable(false);
        rs.setMessage(message);
        return rs;
    }
}
