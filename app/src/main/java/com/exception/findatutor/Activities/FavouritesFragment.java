package com.exception.findatutor.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;

import java.util.ArrayList;

public class FavouritesFragment extends Fragment {
    private RecyclerView myRecyclerView;
    private ArrayList<TutorCards> listitems = new ArrayList<>();
    int Images[] = {R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five};
    private MongoDB mongoDB;
    private ArrayList<String> array;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mongoDB = MongoDB.getInstance();

        getActivity().setTitle("Favourites");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_tutors, container, false);

        myRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        myRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        new FetchFavourites().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        myRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<TutorCards> list;

        public MyAdapter(ArrayList<TutorCards> Data) {
            list = Data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tutor_cards, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            holder.titleTextView.setText(list.get(position).getTutorName());
            holder.titleCourseView.setText(list.get(position).getCourses());
            holder.titleFeeView.setText(list.get(position).getFee());
            // holder.titleCityView.setText(list.get(position).getCardAge());
            holder.coverImageView.setImageResource(list.get(position).getImageResourceId());
            holder.coverImageView.setTag(list.get(position).getImageResourceId());
//                holder.likeImageView.setTag(R.drawable.username);

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView titleCityView;
        public ImageView coverImageView;
        public ImageView likeImageView;
        public ImageView shareImageView;
        public TextView titleCourseView;
        public TextView titleFeeView;

        public MyViewHolder(View v) {
            super(v);
            LinearLayout linear = (LinearLayout) v.findViewById(R.id.linearlayoutfull);
            titleTextView = (TextView) v.findViewById(R.id.tv_card_name);
            titleCourseView = (TextView) v.findViewById(R.id.tv_subject_card);
            titleFeeView = (TextView) v.findViewById(R.id.tv_fee_card);
            coverImageView = (ImageView) v.findViewById(R.id.coverImageView);
            likeImageView = (ImageView) v.findViewById(R.id.likeImageView);
            likeImageView.setImageResource(R.drawable.substract);

            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new RemoveFavourite(titleTextView.getText().toString()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    Snackbar snackbar = Snackbar
                            .make(view, "Removed from favourites", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });
            linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getActivity(), "Opening details of" + titleTextView.getText(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(), CardProfile.class);
                    Bundle dataBundle = new Bundle();
                    //for image
                    coverImageView.buildDrawingCache();
                    Bitmap image = coverImageView.getDrawingCache();
                    dataBundle.putParcelable("image", image);

                    dataBundle.putString("title", titleTextView.getText().toString());
                    dataBundle.putString("courses", titleCourseView.getText().toString());
                    dataBundle.putString("fee", titleFeeView.getText().toString());
                    intent.putExtras(dataBundle);
                    v.getContext().startActivity(intent);

                }
            });
        }
    }

    private class RemoveFavourite extends AsyncTask<String, String, ArrayList<String>> {
        String s;
        String index = "0";

        RemoveFavourite(String s) {
            this.s = s;
        }

        @Override
        protected ArrayList<String> doInBackground(String... voids) {

                mongoDB.RemoveFavourite(s, "no");
                return mongoDB.removefromfavorites(s);

        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {

        }
    }


    private class FetchFavourites extends AsyncTask<String, String, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... voids) {
            return mongoDB.retrieveFavouriteList();
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            array = strings;
            System.out.println("username size :" + array.size());
            listitems.clear();
            if(array != null)
            for(int i = 0;i< array.size(); i++) {
                new FetchFavouritesTutor(array.get(i)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

        }
    }

    private class FetchFavouritesTutor extends AsyncTask<Object, Object, ArrayList<TutorInfoFull>> {

        String i ;
        public FetchFavouritesTutor(String i) {
            this.i = i;
        }

        @Override
        protected ArrayList<TutorInfoFull> doInBackground(Object... voids) {
            System.out.println("i is " +i);
            return mongoDB.retrieveOneTutorData(i);
        }

        @Override
        protected void onPostExecute(ArrayList<TutorInfoFull> tutorInfoFull) {
            for (TutorInfoFull alluser : tutorInfoFull) {
                //TutorInfoFull alluser = tutorInfoFull;
                TutorCards item = new TutorCards();
                item.setTutorName(alluser.getTutorName());
                item.setCourses(alluser.getCourses());
                item.setFee(alluser.getFee());
                item.setImageResourceId(Images[0]);
                listitems.add(item);
            }
            System.out.println("Size1 : " + listitems.size());
            if (listitems.size() > 0 & myRecyclerView != null) {
                myRecyclerView.setAdapter(new MyAdapter(listitems));
            }
        }
        }
    }
