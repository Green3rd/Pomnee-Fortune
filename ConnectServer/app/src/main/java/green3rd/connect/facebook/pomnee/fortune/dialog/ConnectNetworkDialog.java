package green3rd.connect.facebook.pomnee.fortune.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import green3rd.connect.facebook.pomnee.fortune.R;

/**
 * Created by Akamu on 3/1/15 AD.
 */
public class ConnectNetworkDialog extends DialogFragment {


    public interface ConnectNetworkDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    ConnectNetworkDialogListener mListener;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.not_have_internet_connection)
                    .setPositiveButton(R.string.go_back_to_connect_internet, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogPositiveClick(ConnectNetworkDialog.this);
                        }
                    })
                    .setNegativeButton(R.string.continue_anyway, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogNegativeClick(ConnectNetworkDialog.this);
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ConnectNetworkDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
