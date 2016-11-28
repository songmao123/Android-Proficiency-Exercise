package com.sqsong.gankiosample.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by 青松 on 2016/11/24.
 */

public class GankBean implements Parcelable {

    private boolean error;
    private List<GankData> results;

    public GankBean() {
    }

    protected GankBean(Parcel in) {
        error = in.readByte() != 0;
        results = in.createTypedArrayList(GankData.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (error ? 1 : 0));
        dest.writeTypedList(results);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GankBean> CREATOR = new Creator<GankBean>() {
        @Override
        public GankBean createFromParcel(Parcel in) {
            return new GankBean(in);
        }

        @Override
        public GankBean[] newArray(int size) {
            return new GankBean[size];
        }
    };

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<GankData> getResults() {
        return results;
    }

    public void setResults(List<GankData> results) {
        this.results = results;
    }

}
