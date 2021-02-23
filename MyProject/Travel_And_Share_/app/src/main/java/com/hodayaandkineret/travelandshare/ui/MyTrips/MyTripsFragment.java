package com.hodayaandkineret.travelandshare.ui.MyTrips;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hodayaandkineret.travelandshare.Model.Model;
import com.hodayaandkineret.travelandshare.Model.Post;
import com.hodayaandkineret.travelandshare.R;
import com.hodayaandkineret.travelandshare.ui.home.HomeFragment;
import com.hodayaandkineret.travelandshare.ui.home.HomeFragmentDirections;
import com.hodayaandkineret.travelandshare.ui.home.PostListViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyTripsFragment extends Fragment {

    ListView list;
    SwipeRefreshLayout sRef;
    myPostListViewModel viewModel;
    MyAdapter adapter;
    TextView name,location;
    ImageView imagev;
    FirebaseUser muser= FirebaseAuth.getInstance().getCurrentUser();
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mytrips, container, false);
        list=view.findViewById(R.id.Fragment_mytrip_list);
        sRef=view.findViewById(R.id.Fragment_mytrip_swiperefresh);
        viewModel=new ViewModelProvider(this).get(myPostListViewModel.class);

        adapter=new MyTripsFragment.MyAdapter();
        list.setAdapter(adapter);
        sRef.setOnRefreshListener(() -> {
            sRef.setRefreshing(true);
            reloadData();
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                MyTripsFragmentDirections.ActionNavMyTripsToMyPostDetailsFragment action=MyTripsFragmentDirections.actionNavMyTripsToMyPostDetailsFragment(viewModel.getMyList().getValue().get(i));
                Navigation.findNavController(view).navigate(action);
            }
        });
        viewModel.getMyList().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
                    @Override
                    public void onChanged(List<Post> posts) {
                        adapter.notifyDataSetChanged();
                    }
                }
        );
        return view;
    }
    public void reloadData(){
        Model.instance.refreshAllUserPost(() -> {
            sRef.setRefreshing(false);
        });
    }
    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (viewModel.getMyList().getValue() == null) {
                return 0;
            } else {
                return viewModel.getMyList().getValue().size();
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = getLayoutInflater();
                view = inflater.inflate(R.layout.post_list_row, null);
            } else {
            }
            Post post = viewModel.getMyList().getValue().get(i);
            name = view.findViewById(R.id.post_list_row_name);
            imagev = view.findViewById(R.id.post_list_row_image);
            location = view.findViewById(R.id.post_list_row_location);
            name.setText(post.getName());
            location.setText(post.getLocation());
            imagev.setTag(post.getImageUrl());

            if (post.getImageUrl() != null && post.getImageUrl() != "") {
                if (post.getImageUrl() == imagev.getTag()) {
                    Picasso.get().load(post.getImageUrl()).into(imagev);
                }
            } else {
            }
            return view;
        }

    }
}