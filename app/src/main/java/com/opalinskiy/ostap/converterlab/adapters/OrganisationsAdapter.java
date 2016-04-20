package com.opalinskiy.ostap.converterlab.adapters;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;


import com.opalinskiy.ostap.converterlab.R;
import com.opalinskiy.ostap.converterlab.interfaces.EventHandler;
import com.opalinskiy.ostap.converterlab.models.organisation.Organisation;

import java.util.List;

public class OrganisationsAdapter extends RecyclerView.Adapter<OrganisationsAdapter.MyViewHolder> {
    private final EventHandler handler;
    private List<Organisation> organisations;
    private Organisation organisation;


    public OrganisationsAdapter(EventHandler handler, List<Organisation> contactList) {
        this.handler = handler;
        this.organisations = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_element_layout, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {

        organisation = organisations.get(i);

        viewHolder.mTxtTitle.setText(organisation.getTitle());
        viewHolder.mTxtRegion.setText(organisation.getRegion());
        if (!organisation.getRegion().equals(organisation.getCity())) {
            viewHolder.mTxtCity.setText(organisation.getCity());
        }
        viewHolder.mTxtPhone.setText("Тел.: " + organisation.getPhone());
        viewHolder.mTxtAddress.setText("Адрес : " + organisation.getAddress());

    }

    @Override
    public int getItemCount() {
        return organisations == null ? 0 : organisations.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements TabLayout.OnTabSelectedListener {

        private CardView mCard;

        private TextView mTxtTitle;
        private TextView mTxtRegion;
        private TextView mTxtCity;
        private TextView mTxtPhone;
        private TextView mTxtAddress;
        private TabLayout mTabLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            mCard = (CardView) itemView.findViewById(R.id.mCard);
            mTxtTitle = (TextView) itemView.findViewById(R.id.txtOrgTitle_IL);
            mTxtRegion = (TextView) itemView.findViewById(R.id.txtRegion_IL);
            mTxtCity = (TextView) itemView.findViewById(R.id.txtCity_IL);
            mTxtPhone = (TextView) itemView.findViewById(R.id.txtPhone_IL);
            mTxtAddress = (TextView) itemView.findViewById(R.id.txtAddress_IL);

            mTabLayout = (TabLayout) itemView.findViewById(R.id.tabsLayout_IL);

            mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.ic_link));
            mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.ic_map));
            mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.ic_phone));
            mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.ic_next));

            mTabLayout.setOnTabSelectedListener(this);
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            checkTabSelected(tab, organisations.get(getAdapterPosition()));
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            checkTabSelected(tab, organisations.get(getAdapterPosition()));
        }
    }

    private void checkTabSelected(TabLayout.Tab tab, Organisation organisation) {
        switch (tab.getPosition()) {
            case 0:
                handler.onOpenLink(organisation);
                break;
            case 1:
                handler.onShowMap(organisation);
                break;
            case 2:
                handler.onCallNumber(organisation);
                break;
            case 3:
                handler.onShowDetails(organisation);
                break;
        }
    }
}