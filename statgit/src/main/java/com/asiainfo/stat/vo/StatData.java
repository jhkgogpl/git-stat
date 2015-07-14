package com.asiainfo.stat.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jacky on 2015/7/7.
 */
public class StatData implements Comparable<StatData> {

    private String name;
    private long add;
    private long del;
    private long file;
    private String first;
    private String last;
    private long[] adds;
    private long[] dels;

    public StatData() {
    }

    public StatData(String name, long add, long del, long file,
                    String first, String last, long[] adds, long[] dels) {
        this.name = name;
        this.add = add;
        this.del = del;
        this.file = file;
        this.first = first;
        this.last = last;
        this.adds = adds;
        this.dels = dels;
    }

    public StatData addData(StatData sd) {
        for (int i = 0; i < this.adds.length; i++) {
            this.adds[i] = this.adds[i] + sd.adds[i];
            this.dels[i] = this.dels[i] + sd.dels[i];
        }
        return new StatData(this.name, this.add + sd.add, this.del + sd.del,
                this.file + sd.file, this.first, sd.last, this.adds, this.dels);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAdd() {
        return add;
    }

    public void setAdd(long add) {
        this.add = add;
    }

    public long getDel() {
        return del;
    }

    public void setDel(long del) {
        this.del = del;
    }

    public long getFile() {
        return file;
    }

    public void setFile(long file) {
        this.file = file;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public long[] getAdds() {
        return adds;
    }

    public void setAdds(long[] adds) {
        this.adds = adds;
    }

    public long[] getDels() {
        return dels;
    }

    public void setDels(long[] dels) {
        this.dels = dels;
    }

    public void initDetail(int ind) {
        this.adds = new long[ind];
        this.dels = new long[ind];
    }

    public void setDetail(int ind, long add, long del) {
        this.adds[ind] = add;
        this.dels[ind] = del;
    }

    @Override
    public String toString() {
        return "name:" + name + "; add:" + add + "; del:" + del + "; files:" + file
                + "; first:" + first + "; last:" + last;
    }

    @Override
    public int compareTo(StatData o) {
        return this.add < o.add ? 1 : -1;
    }

}
