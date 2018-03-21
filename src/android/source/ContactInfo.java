package com.sc.contacts;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lizhen on 2018/3/17.
 */

public class ContactInfo implements Parcelable {
    private String contactName;
    private ArrayList<String> phoneNos = new ArrayList<String>();

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public ArrayList<String> getPhoneNos() {
        return phoneNos;
    }

    public void setPhoneNo(ArrayList<String> phoneNos) {
        this.phoneNos = phoneNos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contactName);
        dest.writeStringList(phoneNos);
    }

    public ContactInfo() {

    }

    public ContactInfo(Parcel source){
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setContactName(source.readString());
        source.readList(new ArrayList<String>(),null);
    }

    public static final Parcelable.Creator<ContactInfo> CREATOR = new Parcelable.Creator<ContactInfo>() {
        @Override
        public ContactInfo[] newArray(int size) {
            return new ContactInfo[size];
        }

        @Override
        public ContactInfo createFromParcel(Parcel source) {
            return new ContactInfo(source);
        }
    };
}
