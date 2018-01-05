package fpt.isc.nshreport.activities.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.adapters.SalesReportDetailAdapter02;
import fpt.isc.nshreport.models.GasPumps;
import fpt.isc.nshreport.models.ImageList;
import fpt.isc.nshreport.models.PumpLinesAdd;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.objectParse.ReportDetail;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;

/**
 * Created by PhamTruong on 25/05/2017.
 */

public class ReportDetailTab1Activity extends BaseFragment {
    //Declares Variables
    @BindView(R.id.lvSalesReportAdd)
    public ExpandableListView lvDetail = null;
    public int lastExpandedPosition = -1;

    private ReportDetail data;
    private int reportID = 0;

    private SalesReportDetailAdapter02 adapter;
    private ArrayList<GasPumps> headerDataList = null;
    private HashMap<GasPumps, ArrayList<PumpLinesAdd>> childDataList = null;

    User user;

    public static ReportDetailTab1Activity newInstance(ReportDetail data_, int reportID_) {

        Bundle args = new Bundle();
        args.putSerializable("data", data_);
        args.putInt("reportID", reportID_);
        ReportDetailTab1Activity fragment = new ReportDetailTab1Activity();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_shift_add_tab1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        user = NSHSharedPreferences.getInstance(getContext()).getUserLogin();
        createEvents();
        getData();
    }

    private void createEvents(){
        lvDetail.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(lastExpandedPosition != -1 && groupPosition != lastExpandedPosition){
                    lvDetail.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }

    private void getData() {
        data = (ReportDetail) getArguments().getSerializable("data");
        reportID = getArguments().getInt("reportID");
        headerDataList = new ArrayList<GasPumps>();
        childDataList = new HashMap<GasPumps, ArrayList<PumpLinesAdd>>();
        try {
            String name_detail = "", litre_detail = "", money_detail = "";
            //set headerDataList
            for (int i = 0; i < data.data.daily_report_details.fuel_filling_columns.size(); i++) {
                GasPumps tmp = new GasPumps();
                tmp.setId(data.data.daily_report_details.fuel_filling_columns.get(i).fuel_filling_column_id);
                tmp.setName(data.data.daily_report_details.fuel_filling_columns.get(i).fuel_filling_column_code);
                headerDataList.add(tmp);
                //add data for detail bottom
                litre_detail = AppUtilities.longTypeTextFormat(data.data.daily_report_details.fuel_filling_columns.get(i).total_liter) + " lít";
                money_detail = "$ " + AppUtilities.longTypeTextFormat(data.data.daily_report_details.fuel_filling_columns.get(i).total_money);
                //

                List<ReportDetail.data.daily_report_details.fuel_filling_columns.pump> pump = data.data.daily_report_details.fuel_filling_columns.get(i).pumps;
                ArrayList<PumpLinesAdd> pumplist = new ArrayList<>();
                for (int j = 0; j < pump.size(); j++) {
                    PumpLinesAdd a = new PumpLinesAdd();
                    //a.setPumpId(pump.get(j).pump_id);
                    a.setPumpId(pump.get(j).daily_report_detail_id);

                    //new image id
                    int imageID = 0;
                    String imageLink = "";
                    List<ImageList> imageList = new ArrayList<>();
                    ImageList tmpImage ;
                    for (int k = 0; k < pump.get(j).images.size(); k++) {
                        tmpImage = new ImageList();
                        tmpImage.setName(pump.get(j).images.get(k).type);
                        tmpImage.setLink(pump.get(j).images.get(k).daily_report_name);
                        imageList.add(tmpImage);

                        if (pump.get(j).images.get(k).type != null && pump.get(j).images.get(k).type.equals("shift_close")) {
                            imageID = pump.get(j).images.get(k).daily_report_image_id;
                            imageLink = pump.get(j).images.get(k).daily_report_name;
                        }
                    }
                    a.setImageID(imageID);
                    a.setPhotos(imageLink);
                    a.setLineName(pump.get(j).pump_name + " " + pump.get(j).location);
                    a.setSalesLitre(pump.get(j).sell_number);
                    a.setApproveNum(pump.get(j).number_approve);
                    a.setReportNum(pump.get(j).number_report);
                    a.setSalesPrice(pump.get(j).current_price);
                    //set images report
                    a.setImageList(imageList);
                    pumplist.add(a);
                }
                childDataList.put(tmp, pumplist);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        showDataList();
        //((BaseActivity)getActivity()).swipeRefreshLayout.setRefreshing(false);

    }

    /**
     * Show data list for App
     */
    private void showDataList() {
        if (headerDataList != null && childDataList != null) {
            adapter = new SalesReportDetailAdapter02(getActivity(), headerDataList, childDataList, reportID);
            lvDetail.setAdapter(adapter);
        }
    }
}
