package fpt.isc.nshreport.activities.Fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.adapters.ShiftAddOtherAdapter;
import fpt.isc.nshreport.models.ShiftAddOtherDetail;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;

/**
 * Created by PhamTruong on 25/05/2017.
 */

public class ShiftAddTab2Activity extends BaseFragment {
    //Declares Variables
    @BindView(R.id.lvAdd)
    ListView lvAdd;
    private ShiftAddOtherAdapter adapter;
    public List<ShiftAddOtherDetail> listData;

    User user;

    public static ShiftAddTab2Activity newInstance(List<ShiftAddOtherDetail> data) {

        Bundle args = new Bundle();
        args.putSerializable("data", (Serializable) data);
        ShiftAddTab2Activity fragment = new ShiftAddTab2Activity();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_shift_add_tab2, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        user  = NSHSharedPreferences.getInstance(getContext()).getUserLogin();

        initView();
        getData();
    }

    public void initView()
    {
        ColorDrawable sage = new ColorDrawable(ContextCompat.getColor(getContext(), android.R.color.transparent));
        lvAdd.setDivider(sage);
        lvAdd.setDividerHeight(10);
    }

    public void getData()
    {
        listData = (List<ShiftAddOtherDetail>) getArguments().getSerializable("data");
        if(listData != null) {
            adapter = new ShiftAddOtherAdapter(getContext(), listData);
            lvAdd.setAdapter(adapter);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }


    @Override
    public void onStart() {
        super.onStart();
    }
}
