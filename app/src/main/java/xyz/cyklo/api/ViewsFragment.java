package xyz.cyklo.api;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import xyz.cyklo.app.cyklo.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ViewsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";


    private TextView info;
    private ImageView screenshot,left_arrow,right_arrow;
    private View rootView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private int mParam2;
    private boolean mParam3,mParam4;

    // private OnFragmentInteractionListener mListener;

    public ViewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewsFragment newInstance(String param1, int param2, boolean param3,boolean param4) {
        ViewsFragment fragment = new ViewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, String.valueOf(param2));
        args.putString(ARG_PARAM3, String.valueOf(param3));
        args.putString(ARG_PARAM4, String.valueOf(param4));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            //set default mParam2 as default drawable
            mParam2 = Integer.parseInt(getArguments().getString(ARG_PARAM2));
            mParam3 = Boolean.parseBoolean(getArguments().getString(ARG_PARAM3));
            mParam4 = Boolean.parseBoolean(getArguments().getString(ARG_PARAM4));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_views, container, false);
        info = (TextView) rootView.findViewById(R.id.info);
        screenshot = (ImageView) rootView.findViewById(R.id.screenshot);
        left_arrow = (ImageView) rootView.findViewById(R.id.left_arrow_image);
        right_arrow = (ImageView) rootView.findViewById(R.id.right_arrow_image);

        info.setText(mParam1);
        //screenshot.setImageResource(mParam2);
        screenshot.setBackgroundResource(mParam2);

        if(mParam3){
            left_arrow.setVisibility(View.VISIBLE);
        }
        if (mParam4) {
            right_arrow.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

}
