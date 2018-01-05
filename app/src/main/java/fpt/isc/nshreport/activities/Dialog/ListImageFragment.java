package fpt.isc.nshreport.activities.Dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.adapters.PhotosListAdapter;
import fpt.isc.nshreport.models.ImageList;
import fpt.isc.nshreport.models.ReportPhotos;

public class ListImageFragment extends DialogFragment {
    //Declares variables
    private ListView lvPhoto;
    private ImageView closedDialog;
    private PhotosListAdapter adapter = null;
    private ArrayList<ReportPhotos> photosArrayList = null;
    private List<ImageList> data;
    private Context context;

    @BindView(R.id.txt_title)
    TextView txt_title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_image, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButterKnife.bind(this,rootView);

        this.context = getActivity();
        data = (List<ImageList>) getArguments().getSerializable("data");

        String title = getArguments().getString("title","");
        if(title!="")
        {
            txt_title.setText(title);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createWidgets(getView());
        //Create events
        createEvents();
        //Get data
        getData();
        //Show Data
        showData();
    }

    /**
     * Create Widgets for App
     */
    private void createWidgets(View view) {
        lvPhoto = (ListView) view.findViewById(R.id.lvPhotosList);
        closedDialog = (ImageView) view.findViewById(R.id.action_closed_photos_list);
        photosArrayList = new ArrayList<ReportPhotos>();
    }

    /**
     * Create Events for App
     */
    private void createEvents() {
        closedDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * Get data for App
     */

    private void getData() {
        ReportPhotos tmp;
        for(int i=0;i<data.size();i++)
        {
            tmp = new ReportPhotos();
            tmp.setUpdateTime(data.get(i).getName());
            tmp.setPhotoPath(data.get(i).getLink());
            photosArrayList.add(tmp);
        }
    }

    /**
     * Show data for App
     */
    private void showData() {
        if (photosArrayList != null && photosArrayList.size() > 0) {
            adapter = new PhotosListAdapter(getActivity(), R.layout.activity_photos_list_item, photosArrayList);
            lvPhoto.setAdapter(adapter);
        }
    }
}
