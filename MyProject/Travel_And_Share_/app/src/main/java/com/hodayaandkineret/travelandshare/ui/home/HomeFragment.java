package com.hodayaandkineret.travelandshare.ui.home;

import android.os.Bundle;
import android.util.Log;
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

import com.hodayaandkineret.travelandshare.Model.Model;
import com.hodayaandkineret.travelandshare.Model.Post;
import com.hodayaandkineret.travelandshare.R;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

public class HomeFragment extends Fragment {

    PostListViewModel viewModel;
    ListView list;
    MyAdapter adapter;
    SwipeRefreshLayout sRef;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        list=view.findViewById(R.id.fragment_home_list);
        sRef=view.findViewById(R.id.fragment_home_swiperRefresh);

        viewModel=new ViewModelProvider(this).get(PostListViewModel.class);
        adapter=new MyAdapter();
        list.setAdapter(adapter);

        sRef.setOnRefreshListener(() -> {
            sRef.setRefreshing(true);
            reloadData();
        });

//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
//                Log.d("TAG","post id"+i);
////                HomeFragmentDirections.ActionNavHomeToPostDetailsFragment action = HomeFragmentDirections.actionNavHomeToPostDetailsFragment(allData.get(i).getPid());
////                Navigation.findNavController(view).navigate(action);
//            }
//        });
        viewModel.getList().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
                    @Override
                    public void onChanged(List<Post> posts) {
                        adapter.notifyDataSetChanged();
                    }
                }
        );
        return view;
    }
    public void reloadData(){
        Model.instance.refreshAllPost(() -> {
            sRef.setRefreshing(false);
        });
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        Model.instance.getAllPosts(new Model.GetAllPostsListener() {
//            @Override
//            public void onComplete(List<Post> _data) {
//                viewModel.setList(_data);
//                adapter.notifyDataSetChanged();
//            }
//        });
//    }
    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if(viewModel.getList().getValue()==null){
                return 0;
            }
            else{
                return viewModel.getList().getValue().size();
            }
        }

        @Override
        public Object getItem(int position) { return null; }

        @Override
        public long getItemId(int position) { return 0; }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null)
            {
                LayoutInflater inflater=getLayoutInflater();
                view=inflater.inflate(R.layout.post_list_row,null);
            }
            else{

            }
            Post post=viewModel.getList().getValue().get(i);
            TextView name=view.findViewById(R.id.post_list_row_name);
            ImageView imagev=view.findViewById(R.id.post_list_row_image);
            TextView location=view.findViewById(R.id.post_list_row_location);
            name.setText(post.getName());
            location.setText(post.getLocation());
            imagev.setTag(post.getImageUrl());
            if (post.getImageUrl() != null && post.getImageUrl() != ""){
                if (post.getImageUrl() == imagev.getTag()) {
                    Picasso.get().load(post.getImageUrl()).into(imagev);
                }
            }else{

            }
            return view;
        }
    }
}