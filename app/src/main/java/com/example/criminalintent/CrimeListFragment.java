package com.example.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CrimeListFragment extends Fragment {
    //used to set filters for LogCat
    private static final String TAG = "CrimeListFragment";
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //questa view deve contenere un "widget" RecycleView (vedi layout fragment_crime_list)
        //il cui riferimento viene salvato in mCrimeRecycleView alla riga 27
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = (RecyclerView) view
                .findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //il metodo updateUI fa uso della classe CrimeAdapter, la quale fa uso della classe
        // CrimeHolder. Esse sono le definizioni specifiche
        //delle classi Adapter e ViewHolder.
        updateUI();
        return view;
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity()); //recupero il singleton
        List<Crime> crimes = crimeLab.getCrimes();
        if (mAdapter == null) {
        mAdapter = new CrimeAdapter(crimes); //costruttore di Adapter a cui passiamo la lista di crimini

        //metodo chiave
        mCrimeRecyclerView.setAdapter(mAdapter);
        //nel momento in cui viene settato l'adapter del RecyclerView, quest'ultimo inizia
        //automaticamente ad invocare i suoi metodi (un po' come faceva il SO quando creava l'attività).
        // Precisamente li invoca in quest'ordine: getItemCount() ; oncreateViewHolder(ViewGroup, int) ;
        //onBindViewHolder(ViewHolder, int);
        } else {
            mAdapter.notifyDataSetChanged(); //viene invocato se l'adapter è già stato impostato in precedenza
        }
    }

    //definizione specifica dell'Adapter
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }
        //2
        //questo metodo viene invocato un numero di volte sufficienti a riempire lo schermo
        //(nel nostro caso 12 al massimo. dico al massimo perchè potrebbe darsi che la lista
        //abbia un numero di elementi inferiore.
        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "CrimeAdapter.onCreateViewHolder() called");
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }
        //3
        //questo metodo viene invocato inizialmente un messimo di 12 volte, e poi ogni volta
        //che scrolliamo la lista.
        //position credo che sia la posizione dell'elemento successivo o precedente della lista.
        //ricordo che è compito del LayoutManager (riga 29) posizionare le le diverse View sullo schermo
        //(In questo caso verticalmente visto che stiamo usando un LinearLayoutManager (riga 29);
        // GridLyoutManager invece li disporrebbe come su una griglia come vedremo)
        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Log.d(TAG, "CrimeAdapter.onBindViewHolder() called");
            Log.d(TAG, "position: " + position);
            Crime crime = mCrimes.get(position);
            holder.mTitleTextView.setText(crime.getTitle());
            holder.bindCrime(crime);
        }
        //1
        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    //definizione specifica del ViewHolder
    //CrimeHolder rappresenta praticamente 1 solo elemento della lista.
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;
        private Crime mCrime;


        //nel costruttore fa un wiring up dei widget
        public CrimeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
          //  Log.d(TAG, "CrimeHolder.CrimeHolder() " + mCrime.getTitle());
            mTitleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView)
                    itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox)
                    itemView.findViewById(R.id.list_item_crime_solved_check_box);
        }
        public void bindCrime(Crime crime) {
            mCrime = crime;
            Log.d(TAG, "CrimeHolder.bindCrime() " + mCrime.getTitle());
            Log.d(TAG, "Id del crimine: " + mCrime.getId());
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }
        //gestisce l'evento click su un qualsiasi elemento della lista (quindi tutti gli event
        // handler dovranno essere gestiti in CrimeHolder attraverso la sovrascrittura di metodi
        //forniti dalla classe ViewHolder)
        @Override
        public void onClick(View v) {
            Log.d(TAG, "CrimeHolder.onClick() called");
            Log.d(TAG, "Titolo del crimine cliccato: " + mCrime.getTitle());
            Log.d(TAG, "Id del crimine cliccato: " + mCrime.getId());
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }
    }

    //viene invocato quando premiamo il pulsante di "back" dopo aver selezionato un elemento
    //della lista ed averne eventualmente modificato i dati.
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}
