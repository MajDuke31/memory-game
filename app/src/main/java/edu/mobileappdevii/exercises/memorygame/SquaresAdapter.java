package edu.mobileappdevii.exercises.memorygame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

// The custom adapter for the Memory Game App
public class SquaresAdapter extends BaseAdapter {
    private final ArrayList<Integer> squareValues; // Holds the values of all of the squares
    private Context context; // Holds the context

    // The SquaresAdapter Constructor
    public SquaresAdapter(Context context, ArrayList<Integer> squareValues) {
        this.context = context;
        this.squareValues = squareValues;
    }

    // Overridden to return the total number of values used in the Memory Game
    @Override
    public int getCount() {
        return this.squareValues.size();
    }

    // The getItem() method (Not being used)
    @Override
    public Object getItem(int position) {
        return null;
    }

    // The getItemId method (Not being used)
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // Used to fill each item in the GridView with a square TextView and background image
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Instantiate the layout inflater used to inflate the layout
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {
            // Inflate the square layout containing each square item
            gridView = inflater.inflate(R.layout.square, null);
            // Fetch a reference to the square TextView item
            TextView squareTextView = (TextView) gridView.findViewById(R.id.squareTextView);
            // Set the square's background image
            squareTextView.setBackgroundResource(R.drawable.square_face_down);
        }
        else {
            // The GridView object has already been created
            gridView = convertView;
        }

        return gridView;
    }
}
