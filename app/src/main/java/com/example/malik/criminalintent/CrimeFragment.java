package com.example.malik.criminalintent;

import android.app.Fragment;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.text.format.DateFormat;

import java.net.URI;
import java.util.UUID;

/**
 * Created by malik on 2/17/16.
 */
public class CrimeFragment extends android.support.v4.app.Fragment {
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckbox;
    private Button mReportButton;
    private Button mSuspectButton;
    public static final String EXTRA_CRIME_ID="com.example.malik.criminalintent.crime_id";
    private static final int REQUEST_CONTACT = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crime_id=(UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime=(Crime)CrimeLab.get(this.getActivity()).getCrime(crime_id);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);
        mDateButton=(Button) v.findViewById(R.id.crime_date);
        DateFormat df=new DateFormat();
        String currentDate=(String)df.format("EEEE yyyy:mm:dd hh:mm:ss",mCrime.getDate());
        mDateButton.setText(currentDate);
        mDateButton.setEnabled(false);

        mSolvedCheckbox=(CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckbox.setChecked(mCrime.isSolved());
        mSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mTitleField=(EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mReportButton=(Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String report=getCrimeReport();
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, report);
                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                startActivity(intent);
            }
        });
        PackageManager packageManager = getActivity().getPackageManager();


        mSuspectButton=(Button)v.findViewById(R.id.crime_suspect);
        final Intent intent=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
       // intent.addCategory(Intent.CATEGORY_HOME);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(intent,REQUEST_CONTACT);
            }
        });

        if(mCrime.getSuspect()!=null)
            mSuspectButton.setText(mCrime.getSuspect());

        if (packageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }



        return v;
    }

    public static CrimeFragment newInstance(UUID id){
        Bundle args =new Bundle();
        args.putSerializable(EXTRA_CRIME_ID,id);

        CrimeFragment crimeFragment=new CrimeFragment();
        crimeFragment.setArguments(args);

        return crimeFragment;
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null && requestCode==REQUEST_CONTACT){
            Uri contactUri=data.getData();
            String [] queryFields=new String[] {ContactsContract.Contacts.DISPLAY_NAME};
            Cursor c=getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);
            try{
                if(c.getCount()==0)
                    return;
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            }finally {
                c.close();
            }
        }
    }

}
