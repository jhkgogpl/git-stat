package com.asiainfo.stat.vo;

import com.asiainfo.common.Util;
import com.asiainfo.constant.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jacky on 2015/7/10.
 *
 * save param data from page
 */
public class StatParam {

    private String method;
    private Date startDate;
    private Date endDate;
    private long days;

    public int calcDays(String sDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.StaticParam.SHORT_DATE_FORMAT);
        Date date = null;
        try {
            date = sdf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Util.calcDifDays(date, this.startDate);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }
}
