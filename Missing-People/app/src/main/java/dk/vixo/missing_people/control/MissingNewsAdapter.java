package dk.vixo.missing_people.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dk.vixo.missing_people.R;
import dk.vixo.missing_people.model.SearchNews;

public class MissingNewsAdapter extends ArrayAdapter<SearchNews> {

    private List<SearchNews> newsAdpList;

    public static class ViewHolder {
        ImageView image;
        TextView name;
        TextView description;
    }

    public MissingNewsAdapter(Context context, ArrayList<SearchNews> news) {
        super(context, 0, news);

        this.newsAdpList = news;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchNews news = newsAdpList.get(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.searchnewsrow, parent, false);

            viewHolder.image = (ImageView) convertView.findViewById(R.id.newsPicture);
            viewHolder.name = (TextView) convertView.findViewById(R.id.newsName);
            viewHolder.description = (TextView) convertView.findViewById(R.id.newsDescription);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (news.getPhoto() != null) {
            Bitmap bm = news.getPhoto();
            Bitmap scaled = Bitmap.createScaledBitmap(bm, 200, 200, true);
            int w = scaled.getWidth();
            int h = scaled.getHeight();
            // Setting post rotate to 90
            Matrix mtx = new Matrix();
            mtx.postRotate(90);
            // Rotating Bitmap
            bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
            viewHolder.image.setImageBitmap(bm);
        } else {
            viewHolder.image.setImageResource(R.mipmap.ic_launcher);
        }
        viewHolder.name.setText(news.getPostUser().getFirstname() + " " + news.getPostUser().getLastname());
        viewHolder.description.setText(news.getDescription());

        return convertView;
    }

    public List<SearchNews> getNewsList() {
        return newsAdpList;
    }

    public void SetNewsList(List<SearchNews> newsList) {
        this.newsAdpList = newsList;
    }
}
