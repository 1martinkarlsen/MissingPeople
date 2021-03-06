package dk.vixo.missing_people.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dk.vixo.missing_people.R;
import dk.vixo.missing_people.model.Missing;

public class MissingListAdapter extends ArrayAdapter<Missing> {

    private List<Missing> missingAdpList;

    public static class ViewHolder {
        ImageView image;
        TextView name;
        TextView description;
    }

    public MissingListAdapter(Context context, ArrayList<Missing> missingPersons) {
        super(context, 0, missingPersons);

        this.missingAdpList = missingPersons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Missing missing = getItem(position);
        Missing missing = missingAdpList.get(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.missinglistrow, parent, false);

            viewHolder.image = (ImageView) convertView.findViewById(R.id.lvImg);
            viewHolder.name = (TextView) convertView.findViewById(R.id.lvName);
            viewHolder.description = (TextView) convertView.findViewById(R.id.lvDescription);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (missing.getPhotoOfMissingPerson() != null) {
            viewHolder.image.setImageBitmap(missing.getPhotoOfMissingPerson());
        } else {
            viewHolder.image.setImageResource(R.mipmap.ic_launcher);
        }
        viewHolder.name.setText(missing.name);
        viewHolder.description.setText(missing.description);

        return convertView;
    }

    public List<Missing> getMissingList() {
        return missingAdpList;
    }

    public void SetMissingList(List<Missing> missingList) {
        this.missingAdpList = missingList;
    }
}
