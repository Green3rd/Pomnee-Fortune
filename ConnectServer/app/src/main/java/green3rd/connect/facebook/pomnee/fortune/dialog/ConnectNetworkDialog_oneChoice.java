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
public class ConnectNetworkDialog_oneChoice extends DialogFragment {



        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.not_have_internet_connection)
                    .setPositiveButton(R.string.go_back_to_connect_internet, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }


}
