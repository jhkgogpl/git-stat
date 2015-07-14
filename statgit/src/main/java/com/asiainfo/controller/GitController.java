package com.asiainfo.controller;

import com.asiainfo.service.IGitService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Jacky on 2015/7/7.
 */
@Controller
public class GitController {

    @Autowired
    @Qualifier("gitStat")
    IGitService gitService;

    @RequestMapping("/")
    @ResponseBody
    public String sayHello() {
        return "Hello World!";
    }

    @RequestMapping("/git")
    public String index() {
        return "redirect:stat/gitstat.html";
    }

    @RequestMapping("/git/stat")
    @ResponseBody
    public String statGit(HttpServletRequest request,
                          @RequestParam(value = "method", required = true) String method,
                          @RequestParam(value = "startDate", required = false) String startDate,
                          @RequestParam(value = "endDate", required = false) String endDate,
                          @RequestParam(value = "name", required = false) String name,
                          @RequestParam(value = "gitRoot", required = true) String gitRoot) {

        JSONObject result = new JSONObject();
        String msg = null;
        int success = 0;
        try {
            success = 1;
            result.put("data", gitService.stat(method, startDate, endDate, name, gitRoot));
        } catch (Exception e) {
            msg = e.getMessage();
            result.put("msg", msg);
            e.printStackTrace();
        }
        result.put("success", success);

        return result.toString();
    }
}
