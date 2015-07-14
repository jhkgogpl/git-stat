package com.asiainfo.service;

import net.sf.json.JSONArray;

/**
 * Created by Jacky on 2015/7/14.
 */
public interface IGitService {
    public JSONArray stat(String method, String startDate,
                          String endDate, String author, String gitRoot) throws Exception;
}
